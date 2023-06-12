# Systems Programming Term Project - Client Side Application

This readme provides instructions on how to use the client-side application for the Systems Programming Term Project. The client-side application allows you to interact with the server and perform various operations on student data.

## Usage

To use the client-side application, please follow the steps below:

### Running with JAR File

1. Ensure that you have Java Development Kit (JDK) 11 or above installed on your system.
2. Open the command-line interface or terminal.
3. Navigate to the directory where the client-side JAR file, `StudentDataManager.jar`, is located.
4. Run the following command to start the client:

      ```
     java -jar StudentDataManager.jar <username> <port_number>
      ```

Replace `<username>` with your desired username and `<port_number>` with the port number on which the server is running.

### Manual Compilation and Execution

1. Ensure that you have Java Development Kit (JDK) 11 or above installed on your system.
2. Open the command-line interface or terminal.
3. Navigate to the directory containing the client-side source code.
4. Compile the source code using the following command:

      ```
     javac *.java
      ```

5. Start the client application by running the following command:

      ```
     java Manager <username> <port_number>
      ```


Replace `<username>` with your desired username and `<port_number>` with the port number on which the server is running.

> Note: The server-side application must be running before starting the client-side application.



## Additional Information

For server-side application usage and available commands, please refer to the server-side readme file.

