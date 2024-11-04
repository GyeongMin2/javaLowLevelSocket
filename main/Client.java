import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Server address
        int port = 10004; // Server port
        String userId = "user123"; // User ID to test

        try (Socket socket = new Socket(serverAddress, port)) {
            // Create input and output streams for communication with the server
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send the user ID to the server
            output.println(userId);

            // Read the response from the server
            String response = input.readLine();
            System.out.println("Response from server: " + response);
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}