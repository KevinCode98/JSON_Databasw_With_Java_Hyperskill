package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

class Main {
    private static final int DATABASE_SIZE = 1000;
    private static String[] database = new String[DATABASE_SIZE];

    public static void main(String[] args) {
        // Set server address and port
        String address = "127.0.0.1";
        int port = 23456;

        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
            System.out.println("Server started!");

            while (true) {
                // Wait for a client to connect
                Socket socket = serverSocket.accept();

                // Create input and output streams
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                // Receive message from the client
                String receivedMessage = input.readUTF();
                System.out.println("Received: " + receivedMessage);

                // Process the message based on its type
                if ("exit".equals(receivedMessage.trim())) {
                    break;
                }

                String response = processCommand(receivedMessage);

                // Send a response to the client
                output.writeUTF(response);
                System.out.println("Sent: " + response);

                // Close the connections
                input.close();
                output.close();
                socket.close();
            }

            // Close the server socket
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Method to process the command received from the client
    private static String processCommand(String command) {
        if ("exit".equals(command.trim())) {
            return "OK";
        }
        String[] tokens = command.split(" ");
        if (tokens.length < 2) {
            return "ERROR";
        }

        String action = tokens[0];
        int index;
        try {
            index = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException exception) {
            return "ERROR";
        }

        switch (action) {
            case "set":
                if (index < 1 || index > DATABASE_SIZE) {
                    return "ERROR";
                } else {
                    set(index, command.substring(tokens[0].length() + tokens[1].length() + 2));
                    return "OK";
                }
            case "get":
                if (index < 1 || index > DATABASE_SIZE || database[index - 1] == null || database[index - 1].isEmpty()) {
                    return "ERROR";
                } else {
                    return database[index - 1];
                }
            case "delete":
                if (index < 1 || index > DATABASE_SIZE) {
                    return "ERROR";
                } else {
                    delete(index);
                    return "OK";
                }
            case "exit":
                return "OK";
            default:
                return "ERROR";
        }
    }

    private static void set(int index, String value) {
        database[index - 1] = value;
    }

    private static void delete(int index) {
        if (index >= 1 && index <= DATABASE_SIZE) {
            database[index - 1] = "";
        }
    }
}