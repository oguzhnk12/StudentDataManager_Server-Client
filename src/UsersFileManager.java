import java.io.*;

public class UsersFileManager {

    private final File usersFile;


    public UsersFileManager(File usersFile) {
        this.usersFile = usersFile;
    }

    public synchronized Object[] authenticateUser(String username, String password) {
        Object[] result = new Object[2];
        String line;
        String[] credential;
        FileReader fileReader;
        BufferedReader reader;
        try {
            fileReader = new FileReader(this.usersFile);
            reader = new BufferedReader(fileReader);

            while ((line = reader.readLine()) != null) {
                credential = line.split(",");
                if (credential[0].equals(username)) {
                    if (credential[1].equals(password)) {
                        result[0] = 0;
                        result[1] = "[SUCCESS] Login successful.";
                    } else {
                        result[0] = 1;
                        result[1] = "[FAILURE] Invalid password.";
                    }
                    reader.close();
                    fileReader.close();
                    return result;
                }
            }
            result[0] = 1;
            result[1] = "[FAILURE] Username not found.";
            reader.close();
            fileReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return result;
    }

    public synchronized String changePassword(String username, String currentPassword, String newPassword) {
        String result;
        FileReader fileReader;
        BufferedReader reader;
        FileWriter fileWriter;
        PrintWriter writer;
        String tempFilePath = "temp";
        File tempFile;
        String line;
        String[] credential;
        result = "";
        try {
            tempFile = new File(tempFilePath);
            fileReader = new FileReader(this.usersFile);
            reader = new BufferedReader(fileReader);
            fileWriter = new FileWriter(tempFile);
            writer = new PrintWriter(fileWriter);
            while ((line = reader.readLine()) != null) {
                credential = line.split(",");
                if (credential[0].equals(username)) {
                    if (credential[1].equals(currentPassword)) {
                        writer.println(String.format("%s,%s", credential[0], newPassword));
                        result = "[SUCCESS] Your password has been changed successfully.";
                    } else {
                        writer.println(line);
                        result = "[FAILURE] The current password you entered is invalid.";
                    }
                } else
                    writer.println(line);
            }
            reader.close();
            fileReader.close();
            writer.close();
            fileWriter.close();
            if (!this.usersFile.delete()) {
                System.out.println("Could not delete file.");
            }
            if (!tempFile.renameTo(this.usersFile)) {
                System.out.println("Could not rename file.");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }


}
