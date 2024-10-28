package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // Set server address and port
        String address = "127.0.0.1";
        int port = 23456;

        try {
            // Create a socket and connect to the server
            Socket socket = new Socket(InetAddress.getByName(address), port);
            System.out.println("Client started!");

            // Create input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Send a request to the server
            String request = "Give me a record # 12";
            output.writeUTF(request);
            System.out.println("Sent: " + request);

            // Receive and print the server's response
            String response = input.readUTF();
            System.out.println("Received: " + response);

            // Close the connection
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
