import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
public class StudentsFileManager {

    private final File studentsFile;

    public StudentsFileManager(File studentsFile) {
        this.studentsFile = studentsFile;
    }

    public synchronized ArrayList<String> displayAll() {
        FileReader fileReader;
        BufferedReader reader;
        String line;
        ArrayList<String> resultBuffer = new ArrayList<>();
        String[] columns;
        String[] fullName;
        try {
            fileReader = new FileReader(this.studentsFile);
            reader = new BufferedReader(fileReader);
            resultBuffer.add(String.format("%-6s%-15s%-15s%-6s%-15s%-8s", "St_id", "Name", "Surname", "CGPA", "Date of Birth", "Gender"));
            resultBuffer.add("=================================================================");
            while ((line = reader.readLine()) != null) {
                columns = line.split(",");
                fullName = columns[1].split("\\s+");
                resultBuffer.add(String.format("%-6s%-15s%-15s%-6s%-15s%-8s", columns[0], fullName[0], fullName[1], columns[2], columns[3], columns[4]));

            }
            reader.close();
            fileReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return resultBuffer;
    }

    public synchronized ArrayList<String> displayStudent(String stdID) {
        FileReader fileReader;
        BufferedReader reader;
        ArrayList<String> resultBuffer = new ArrayList<>();
        String line;
        String[] columns;
        String[] fullName;
        boolean found = false;
        try {
            fileReader = new FileReader(this.studentsFile);
            reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                columns = line.split(",");
                if (columns[0].equals(stdID)) {
                    fullName = columns[1].split("\\s+");
                    resultBuffer.add(String.format("%-6s%-15s%-15s%-6s%-15s%-8s", "St_id", "Name", "Surname", "CGPA", "Date of Birth", "Gender"));
                    resultBuffer.add("=================================================================");
                    resultBuffer.add(String.format("%-6s%-15s%-15s%-6s%-15s%-8s", columns[0], fullName[0], fullName[1], columns[2], columns[3], columns[4]));
                    found = true;
                    break;
                }
            }
            if (!found) {
                resultBuffer.add("[FAILURE] Student not found.");
            }
            reader.close();
            fileReader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return resultBuffer;
    }

    public synchronized String changeCGPA(String enteredStdudentId, String value) {
        RandomAccessFile randomAccessFile;
        DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
        boolean found = false;
        byte[] studentIdByte = new byte[4];
        byte[] currentGradeByte = new byte[4];
        String currentCGPA = "";
        String studentId;
        double newCgpa;
        String newCgpaStr;
        try {
            newCgpa = Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return "[FAILURE] Invalid CGPA.";
        }
        if (newCgpa > 4.00 || newCgpa < 0.00) {
            return "[FAILURE] Entered CGPA is out of range. Please enter a CGPA between 0.00-4.00.";
        }
        newCgpaStr = decimalFormat.format(newCgpa);
        try {
            randomAccessFile = new RandomAccessFile(this.studentsFile, "rw");
            while (randomAccessFile.getFilePointer() <= randomAccessFile.length() - 2) {
                randomAccessFile.read(studentIdByte, 0, 4);
                studentId = new String(studentIdByte);
                if (studentId.equals(enteredStdudentId)) {
                    randomAccessFile.seek(randomAccessFile.getFilePointer() + 32);
                    randomAccessFile.read(currentGradeByte, 0, 4);
                    currentCGPA = new String(currentGradeByte);
                    randomAccessFile.seek(randomAccessFile.getFilePointer() - 4);
                    randomAccessFile.write(newCgpaStr.getBytes(StandardCharsets.UTF_8));
                    found = true;
                    break;
                }
                randomAccessFile.seek(randomAccessFile.getFilePointer() + 51);
            }
            if (!found) {
                return "[FAILURE] Student not found.";
            }

            randomAccessFile.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "[SUCCESS] Student " + enteredStdudentId + " CGPA(" + currentCGPA + ") changed to " + newCgpaStr + ".";
    }

    public synchronized String addStudent(String studentID, String name, String surname, String enteredCgpa, String dob, String gender) {
        double cgpa;
        String cgpaStr;
        String fullName;
        String line;
        FileReader fileReader;
        BufferedReader reader;
        FileWriter fileWriter;
        PrintWriter writer;
        boolean found = false;
        String returnMessage = "";
        String[] fields;
        String datePattern = "\\d{2}-\\d{2}-\\d{4}";
        DecimalFormat decimalFormat = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.US));
        if (!studentID.matches("\\d+") || studentID.length() != 4)
            return "[FAILURE] Invalid Stundent ID.";
        if (name.length() + surname.length() > 30)
            return "[FAILURE] The combined length of the Name and Surname fields exceeds the allowed limit. Please ensure that the total number of characters does not exceed 30.";
        try {
            cgpa = Double.parseDouble(enteredCgpa);
        } catch (NumberFormatException exception) {
            return "[FAILURE] Invalid CGPA.";
        }
        if (cgpa > 4.00 || cgpa < 0.00) {
            return "[FAILURE] Entered CGPA is out of range. Please enter a CGPA between 0.00-4.00";
        }
        cgpaStr = decimalFormat.format(cgpa);
        if (!dob.matches(datePattern))
            return "[FAILURE] The provided date of birth format is incorrect. Please ensure that the date follows the format 'dd-mm-YYYY'.";
        if (!gender.equals("M") && !gender.equals("F"))
            return "[FAILURE] The provided gender is invalid. Please enter 'M' for male or 'F' for female to indicate the gender.";
        fullName = String.join(" ", name, surname);
        String studentRecord = String.format("%4s,%-30s,%4s,%10s,%1s", studentID, fullName, cgpaStr, dob, gender);
        try {
            fileReader = new FileReader(this.studentsFile);
            reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null){
                fields = line.split(",");
                if (fields[0].equals(studentID)){
                    found = true;
                    break;
                }
            }
            reader.close();
            fileReader.close();
            if (found){
                returnMessage = "[FAILURE] The provided student ID is already associated with another student in the system.";
            }
            else{
                fileWriter = new FileWriter(this.studentsFile, true);
                writer = new PrintWriter(fileWriter);
                writer.println(studentRecord);
                writer.close();
                fileWriter.close();
                returnMessage = "[SUCCESS] The student has been successfully added to the file.";
            }

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return returnMessage;
    }

    public synchronized String deleteStudent(String studentID) {
        FileReader fileReader;
        FileWriter fileWriter;
        BufferedReader reader;
        PrintWriter writer;
        String line;
        String[] fields;
        File tempFile = new File("temp");
        boolean found = false;
        String returnMessage = "";
        try {
            fileReader = new FileReader(studentsFile);
            fileWriter = new FileWriter(tempFile);
            reader = new BufferedReader(fileReader);
            writer = new PrintWriter(fileWriter);
            while ((line = reader.readLine()) != null) {
                fields = line.split(",");
                if (fields[0].equals(studentID)) {
                    found = true;
                    continue;
                }
                writer.println(line);
            }
            if (!found)
                returnMessage = "[FAILURE] No student records were found with the provided student ID.";
            else
                returnMessage = "[SUCCESS] The student has been successfully deleted from the file.";
            if (!studentsFile.delete()) {
                System.out.println("Could not delete file.");
                returnMessage = "[ERROR] An unexpected error occurred while processing your request.";
            }
            if (!tempFile.renameTo(this.studentsFile)) {
                System.out.println("Could not rename file.");
                returnMessage = "[ERROR] An unexpected error occurred while processing your request.";
            }
            reader.close();
            writer.close();
            fileReader.close();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return  returnMessage;

    }
}
