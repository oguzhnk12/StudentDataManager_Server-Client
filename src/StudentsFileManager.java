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
            resultBuffer.add("==============================================================");
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
                    resultBuffer.add("==============================================================");
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
            return "[FAILURE] Invalid gcpa.";
        }
        if (newCgpa > 4.00 || newCgpa < 0.00) {
            return "[FAILURE] Entered CGPA is out of range. Please enter a CGPA between 0.00-4.00";
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

    public synchronized String addStudent(String studentID, String name, String surname, String cgpa, String dob, String gender) {
        return "";
    }

}
