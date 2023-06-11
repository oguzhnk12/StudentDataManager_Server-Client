# Systems Programming Term Project - Server Side Application

This project is part of the Systems Programming course and involves the development of a server-side application for file access. The server will handle operations on a student file, including adding, deleting, modifying, and displaying student information.


## Usage

To use the server-side application, follow the instructions below:

### Option 1: Using JAR File

1. Locate the JAR file in the `out` directory of the project.
2. Open the command-line interface or terminal.
3. Run the following command to start the server:
      ```
     java -jar StudentDataServer.jar <port_number> <students_file> <users_file>
      ```
4. Replace `<port_number>` with the desired port number to listen on, `<students_file>` with the path to the students file, and `<users_file>` with the path to the file containing username-password pairs for authentication.

### Option 2: Manual Compilation and Execution

1. Ensure you have Java Development Kit (JDK) installed on your system.
2. Open the command-line interface or terminal.
3. Navigate to the project directory containing the source code.
4. Compile the source code using the following command:

      ```
     javac *.java
      ```
5. Start the server by running the following command:
      ```
     java Server <port_number> <students_file> <users_file>
      ```
## Available Commands


- Here are the available commands on the server:
    - `display`: Displays student information.
    - `pwd`: Changes the password.
    - `quit`: Quits the application.
    - `change`: Changes the CGPA of a student.
    - `add`: Adds a new student to the file.
    - `delete`: Deletes a student from the file.

## Student Information

- Name: Oğuzhan
- Surname: Kaya
- Student Number: 21806449