import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Manager {

    public static void main(String[] args) {
        int portNumber = 0;
        String username = "";
        String hostName = "";
        if (args.length >= 2) {
            username = args[0];
            try {
                portNumber = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid port number: " + args[0] + ". Please enter a valid integer value for the port.");
                System.exit(1);
            }
            hostName = args.length == 3 ? args[2] : "localhost";

        } else {
            System.out.println("[ERROR]Please run the server with the required arguments. The template is as follows:");
            System.out.println("Usage: java -jar StudentDataManager.jar <username> <port_number> [host_name]");
            System.exit(1);
        }
        try(Socket socket = new Socket(hostName, portNumber)){
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startSession(username, writer,reader);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private static void startSession(String username, PrintWriter writer, BufferedReader reader){
        Scanner scanner = new Scanner(System.in);
        boolean authenticated  = login(username, writer, reader, scanner);
        String command;
        String numberOfLines;
        String response;
        boolean flag;
        try {
            while (authenticated) {
                command = scanner.nextLine();
                if (command.trim().split(" ")[0].equalsIgnoreCase("QUIT")) {
                    writer.println("QUIT");
                    writer.flush();
                    System.out.println("Disconnecting from server...");
                    break;
                }
                writer.println(command);
                writer.flush();
                numberOfLines = reader.readLine();
                if (numberOfLines == null)
                    break;
                flag = false;
                for(int i = 0; i < Integer.parseInt(numberOfLines.trim()); i++){
                    response = reader.readLine();
                    if (response == null){
                        flag = true;
                        break;
                    }
                    System.out.println(response);
                }
                if (flag)
                    break;
            }
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }
    private static boolean login(String username, PrintWriter writer, BufferedReader reader, Scanner scanner){
        boolean authenticated = false;
        String response;
        String result;
        String message;
        try {
            writer.println(username);
            writer.flush();
            while (!authenticated){
                response = reader.readLine();
                if (response == null)
                    break;
                System.out.println(response);
                writer.println(scanner.nextLine());
                writer.flush();
                result = reader.readLine();
                if (result == null)
                    break;
                message = reader.readLine();
                if (message == null)
                    break;
                if (result.trim().equals("true"))
                    authenticated = true;
                System.out.println(message);
            }
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
        return authenticated;
    }

}

