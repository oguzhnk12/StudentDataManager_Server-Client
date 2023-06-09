import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        openServer(args);
    }

    public static void openServer(String args[]){
        int portNumber = 0;
        if (args.length >= 3) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid port number: " + args[0] + ". Please enter a valid integer value for the port.");
                System.exit(1);
            }
            StudentsFileManager studentsFileManager =  new StudentsFileManager(createFile(args[1]));
            UsersFileManager usersFileManager  = new UsersFileManager(createFile(args[2]));
            try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
                while (true) {
                    Socket incoming = serverSocket.accept();
                    System.out.println("Client connected: " + incoming.getInetAddress().getHostAddress() + " on port " + incoming.getPort());
                    new Thread(new ClientHandler(incoming, studentsFileManager, usersFileManager)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[ERROR]Please run the server with the required arguments. The template is as follows:");
            System.out.println("java -jar StudentDataServer.jar <port_number> <students_file_path> <users_file_path>");
            System.exit(1);
        }
    }

    public static File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[ERROR] " + filePath + " not found. Please provide a valid file path.");
            System.exit(1);
        }
        return file;
    }
}



