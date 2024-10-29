package client;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * This class implements java Client server.
 * */

public class Main {
    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 1024;
        Socket socket = new Socket(InetAddress.getByName(address), port);
        System.out.println("Client started!");

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        CommandLineArgs commandLineArgs = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(commandLineArgs)
                .build()
                .parse(args);

        String command = commandLineArgs.buildCommand();
        System.out.println("Sent: " + command);
        out.writeUTF(command);

        String response = in.readUTF();
        System.out.println("Received: " + response);

        in.close();
        out.close();
        socket.close();
    }
}

