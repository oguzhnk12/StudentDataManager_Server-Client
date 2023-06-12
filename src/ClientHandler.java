import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private static String username;
    private static Socket incoming;
    private static StudentsFileManager studentsFileManager;
    private static UsersFileManager usersFileManager;


    public ClientHandler(Socket incoming, StudentsFileManager studentsFileManager, UsersFileManager usersFileManager) {
        ClientHandler.incoming = incoming;
        ClientHandler.studentsFileManager = studentsFileManager;
        ClientHandler.usersFileManager = usersFileManager;
    }

    @Override
    public void run() {
        BufferedReader reader;
        PrintWriter writer;
        boolean authenticated;
        String response;
        String[] responseFields;
        String[] args;
        Map<String, Command> commands = new HashMap<>() {{
            put("HELP", new Help());
            put("DISPLAY", new Display());
            put("PWD", new ChangePassword());
            put("CHANGE", new Change());
            put("ADD", new Add());
            put("DELETE", new Delete());
        }};
        boolean found;
        try {
            reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
            authenticated = loginUser(reader, writer);
            while (authenticated) {
                response = reader.readLine();
                if (response == null)
                    break;
                responseFields = response.trim().split("\\s+");
                args = new String[responseFields.length - 1];
                System.arraycopy(responseFields, 1, args, 0, responseFields.length - 1);
                found = false;
                for (Map.Entry<String, Command> entry : commands.entrySet()) {
                    if (responseFields[0].equalsIgnoreCase(entry.getKey())) {
                        entry.getValue().execute(args, writer);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if (responseFields[0].equalsIgnoreCase("QUIT"))
                        break;
                    writer.println(1);
                    writer.println("[ERROR] Invalid command. Please type 'HELP' to view the list of available commands and their usage.");
                    writer.flush();
                }
            }
            System.out.println(incoming.getInetAddress().getHostAddress() + ":" + incoming.getPort() + " disconnected.");
            reader.close();
            writer.close();
            incoming.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    private boolean loginUser(BufferedReader reader, PrintWriter writer) {
        boolean authenticated = false;
        int responseCode = 1;
        String password;
        Object[] result;
        try {
            // GET USERNAME
            ClientHandler.username = reader.readLine();
            while (ClientHandler.username != null && responseCode == 1) {
                // ASK FOR PASSWORD
                writer.println("Please Enter Your Password.");
                writer.flush();
                // GET PASSWORD
                password = reader.readLine();
                if (password == null)
                    break;
                // AUTHENTICATE USER
                result = usersFileManager.authenticateUser(ClientHandler.username, password);
                responseCode = (int) result[0];
                // SEND THE RESULT MESSAGE
                if (responseCode == 0)
                    authenticated = true;
                writer.println(authenticated);
                writer.println(result[1]);
                writer.flush();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return authenticated;
    }

    private static class ChangePassword implements Command {

        @Override
        public void execute(String[] args, PrintWriter writer) {
            if (args.length == 2) {
                writer.println(1);
                writer.println(usersFileManager.changePassword(ClientHandler.username, args[0], args[1]));
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid 'pwd' command. Please type 'HELP' to view the list of available commands and their usage.");
                writer.flush();
            }
        }
    }

    private static class Help implements Command {
        @Override
        public void execute(String[] args, PrintWriter writer) {
            writer.println(24);
            writer.println("=== Help ===\n");
            writer.println("Available commands:");
            writer.println("- display: Displays student information.");
            writer.println("    Usage:");
            writer.println("        display                                                 : Displays information of all students.");
            writer.println("        display <student_id>                                    : Displays information of a specific student by their ID.\n");
            writer.println("- pwd: Changes the password.");
            writer.println("    Usage:");
            writer.println("        pwd <current_password> <new_password>                   : Changes the current password to a new one.\n");
            writer.println("- quit: Quits the application.");
            writer.println("    Usage:");
            writer.println("        quit                                                    : Quits the application.");
            writer.println("- change: Changes student CGPA.");
            writer.println("    Usage:");
            writer.println("        change <student_id> <new_cgpa>                          : Chages the CGPA of the entered student.");
            writer.println("- add: Adds new student to the file.");
            writer.println("    Usage:");
            writer.println("        add <student_id> <name> <surname> <cgpa> <dob> <gender> : Adds new student to the file based on given information.");
            writer.println("- delete: Deletes a student from the file.");
            writer.println("    Usage:");
            writer.println("        delete <student_id>                                     : Deletes a student whose student ID matches the entered student ID.");
            writer.flush();
        }
    }

    private static class Add implements Command {

        @Override
        public void execute(String[] args, PrintWriter writer) {
            if (args.length == 6) {
                writer.println(1);
                writer.println(studentsFileManager.addStudent(args[0], args[1], args[2], args[3], args[4], args[5]));
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid 'add' command. Please type 'HELP' to view the list of available commands and their usage.");
                writer.flush();
            }
        }
    }

    private static class Delete implements Command {

        @Override
        public void execute(String[] args, PrintWriter writer) {
            if (args.length == 1) {
                writer.println(1);
                writer.println(studentsFileManager.deleteStudent(args[0]));
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid 'delete' command. Please type 'HELP' to view the list of available commands and their usage.");
                writer.flush();
            }
        }
    }

    private static class Display implements Command {
        @Override
        public void execute(String[] args, PrintWriter writer) {
            ArrayList<String> lines;
            if (args.length == 1) {
                lines = studentsFileManager.displayStudent(args[0]);
                writer.println(lines.size());
                for (String eachLine : lines)
                    writer.println(eachLine);
                writer.flush();
            } else if (args.length == 0) {
                lines = studentsFileManager.displayAll();
                writer.println(lines.size());
                for (String eachLine : lines)
                    writer.println(eachLine);
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid 'display' command. Please type 'HELP' to view the list of available commands and their usage.");
                writer.flush();
            }
        }
    }

    private static class Change implements Command {

        @Override
        public void execute(String[] args, PrintWriter writer) {
            if (args.length == 2) {
                writer.println(1);
                writer.println(studentsFileManager.changeCGPA(args[0], args[1]));
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid 'change' command. Please type 'HELP' to view the list of available commands and their usage.");
                writer.flush();
            }
        }
    }

}
