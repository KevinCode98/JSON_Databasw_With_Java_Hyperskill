package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Set server address and port
        String address = "127.0.0.1";
        int port = 23456;

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
            System.out.println("Server started!");

            // Wait for a client to connect
            Socket socket = serverSocket.accept();

            // Create input and output streams
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Receive message from the client
            String receivedMessage = input.readUTF();
            System.out.println("Received: " + receivedMessage);

            // Process the message (assuming the message is in the format "Give me a record # N")
            // Extract the record number N from the message
            int recordNumber = Integer.parseInt(receivedMessage.replaceAll("\\D+", ""));

            // Send a response to the client
            String response = "A record # " + recordNumber + " was sent!";
            output.writeUTF(response);
            System.out.println("Sent: " + response);


            // Close the connections
            input.close();
            output.close();
            socket.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}