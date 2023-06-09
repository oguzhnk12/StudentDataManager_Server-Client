import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
            resultBuffer.add(String.format("%6s%10s%10s%4s%4s%4s%4s", "St_id", "Name", "Surname", "MT", "Q1", "Q2", "Fin"));
            resultBuffer.add("==============================================");
            while ((line = reader.readLine()) != null) {
                columns = line.split(",");
                fullName = columns[1].split(" ");
                resultBuffer.add(String.format("%6s%10s%10s%4s%4s%4s%4s", columns[0], fullName[0], fullName[1], columns[3], columns[4], columns[5], columns[6]));
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
                    fullName = columns[1].split(" ");
                    resultBuffer.add(String.format("%6s%10s%10s%4s%4s%4s%4s", "St_id", "Name", "Surname", "MT", "Q1", "Q2", "Fin"));
                    resultBuffer.add("==============================================");
                    resultBuffer.add(String.format("%6s%10s%10s%4s%4s%4s%4s", columns[0], fullName[0], fullName[1], columns[3], columns[4], columns[5], columns[6]));
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

    public synchronized String changeGrade(String enteredStdudentId, String examType, String newGrade){
        RandomAccessFile randomAccessFile;
        boolean found = false;
        byte[] studentIdByte = new byte[4];
        byte[] currentGradeByte = new byte[2];
        String currentGrade = "";
        String studentId;
        int grade;
        int jump;
        if(examType.equalsIgnoreCase("MT"))
            jump = 28;
        else if (examType.equalsIgnoreCase("Q1"))
            jump = 31;
        else if (examType.equalsIgnoreCase("Q2"))
            jump = 34;
        else if (examType.equalsIgnoreCase("FIN"))
            jump = 37;
        else
            return "[FAILURE] Invalid exam type.";

        if (newGrade.length() == 1)
            newGrade = "0" + newGrade;
        try{
            grade = Integer.parseInt(newGrade);
        }catch (NumberFormatException exception){
            return "[FAILURE] Please enter a number.";
        }
        if (grade >= 100 || grade < 0){
            return "[FAILURE] Entered grade is out of range. Please enter a grade between 0-100";
        }
        try{
            randomAccessFile = new RandomAccessFile(this.studentsFile, "rw");
            while(randomAccessFile.getFilePointer() <= randomAccessFile.length()-2){
                randomAccessFile.read(studentIdByte,0,4);
                studentId = new String(studentIdByte);
                if(studentId.equals(enteredStdudentId)){
                    randomAccessFile.seek(randomAccessFile.getFilePointer() + jump);
                    randomAccessFile.read(currentGradeByte, 0, 2);
                    currentGrade = new String(currentGradeByte);
                    randomAccessFile.seek(randomAccessFile.getFilePointer()  - 2);
                    randomAccessFile.write(newGrade.getBytes(StandardCharsets.UTF_8));
                    found = true;
                    break;
                }
                randomAccessFile.seek(randomAccessFile.getFilePointer() + 41);
            }
            if (!found) {
                return "[FAILURE] Student not found.";
            }

            randomAccessFile.close();
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
        return "[SUCCESS] Student " + enteredStdudentId + " " + examType + " grade changed " + currentGrade + " to " + newGrade + ".";
    }
}
