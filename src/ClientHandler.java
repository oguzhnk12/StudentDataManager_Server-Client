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

    private static BufferedReader reader;
    private static PrintWriter writer;

    private final Map<String, Command> commands;

    public ClientHandler(Socket incoming, StudentsFileManager studentsFileManager, UsersFileManager usersFileManager) {
        ClientHandler.incoming = incoming;
        ClientHandler.studentsFileManager = studentsFileManager;
        ClientHandler.usersFileManager = usersFileManager;
        commands = new HashMap<>() {{
            put("HELP", new Help());
            put("DISPLAY", new Display());
            put("PWD", new ChangePassword());
            put("QUIT", new Quit());
            put("CHANGE", new Change());
        }};
        try {
            reader = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    @Override
    public void run() {
        boolean authenticated;
        String response;
        String[] responseFields;
        String[] args;
        boolean found;
        try {
            authenticated = loginUser(reader, writer);
            while(authenticated){
                response = reader.readLine();
                if (response == null)
                    break;
                responseFields = response.trim().split(" ");
                args = new String[responseFields.length - 1];
                System.arraycopy(responseFields, 1, args, 0, responseFields.length - 1);
                found = false;
                for (Map.Entry<String, Command> entry : commands.entrySet()) {
                    if(responseFields[0].equalsIgnoreCase(entry.getKey())){
                        entry.getValue().execute(args);
                        found = true;
                        if(entry.getKey().equals("QUIT"))
                            authenticated = false;
                        break;
                    }
                }
                if(!found){
                    writer.println(1);
                    writer.println("[ERROR] Invalid command. You can type 'HELP' to see possible commands.");
                    writer.flush();
                }
            }
            System.out.println(incoming.getInetAddress().getHostAddress() + ":" + incoming.getPort() + " disconnected.");
            reader.close();
            writer.close();
            incoming.close();
            if(authenticated)
                usersFileManager.logoutUser(ClientHandler.username);
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
        }catch (IOException exception) {
            exception.printStackTrace();
        }
        return  authenticated;
    }

    private static class Quit implements Command {

        @Override
        public void execute(String[] args) {
            usersFileManager.logoutUser(ClientHandler.username);
        }
    }

    private static class ChangePassword implements Command {
        @Override
        public void execute(String[] args) {
            if (args.length == 2) {
                writer.println(1);
                writer.println(usersFileManager.changePassword(ClientHandler.username, args[0], args[1]));
                writer.flush();
            } else {
                writer.println(1);
                writer.println("[ERROR] Invalid Change Command.");
                writer.flush();
            }
        }
    }
    private static class Help implements Command {
        @Override
        public void execute(String[] args) {
            writer.println(18);
            writer.println("=== Help ===\n");
            writer.println("Available commands:");
            writer.println("- display: Displays student information.");
            writer.println("    Usage:");
            writer.println("        display                                      : Displays information of all students.");
            writer.println("        display <student_id>                         : Displays information of a specific student by their ID.\n");
            writer.println("- pwd: Changes the password.");
            writer.println("    Usage:");
            writer.println("        pwd <current_password> <new_password>        : Changes the current password to a new one.\n");
            writer.println("- quit: Quits the application.");
            writer.println("    Usage:");
            writer.println("        quit                                         : Quits the application.");
            writer.println("- change: Changes student grade.");
            writer.println("    Usage:");
            writer.println("        change <student_id> <exam_type> <new_grade>  : Quits the application.");
            writer.flush();
        }
    }

    private static class Display implements Command {
        @Override
        public void execute(String[] args) {
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
                writer.println("[ERROR] Invalid display command.");
                writer.flush();
            }
        }
    }

    private static class Change implements Command{

        @Override
        public void execute(String[] args) {
            if (args.length == 3){
                writer.println(1);
                writer.println(studentsFileManager.changeGrade(args[0], args[1], args[2]));
                writer.flush();
            }
            else{
                writer.println(1);
                writer.println("[ERROR] Invalid change command.");
                writer.flush();
            }
        }
    }

}
