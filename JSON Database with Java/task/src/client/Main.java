package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Main {
    @Parameter(names = "-t", description = "Type of request (set, get, delete, exit)", required = true)
    private String type;

    @Parameter(names = "-i", description = "Index of the cell", required = false)
    private int index;

    @Parameter(names = "-m", description = "Text for set request")
    private String message;

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        processCommand(main);
    }

    // Method to process the command based on its type
    private static void processCommand(Main main) {
        String command;
        if ("exit".equals(main.type)) {
            command = "exit";
        } else {
            switch (main.type) {
                case "get":
                    command = "get " + main.index;
                    break;
                case "set":
                    command = "set " + main.index + " " + main.message;
                    break;
                case "delete":
                    command = "delete " + main.index;
                    break;
                default:
                    System.out.println("Invalid command");
                    return;
            }
        }

        // Implement the logic to send the request to the server and receive the response
        System.out.println("Client started!");
        System.out.println("Sent: " + command);

        try (Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 23456);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            // Send the request to the server
            output.writeUTF(command);

            // Receive and print the server's response
            if (!"exit".equals(main.type)) {
                String response = input.readUTF();
                System.out.println("Received: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
