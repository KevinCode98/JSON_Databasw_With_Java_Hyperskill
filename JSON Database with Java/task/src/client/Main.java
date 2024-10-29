package client;

import com.beust.jcommander.JCommander;
import server.cli.requests.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {

        System.out.println("Client Started");

        Request request = new Request();

        JCommander.newBuilder()
                .addObject(request)
                .build()
                .parse(args);

        try (
                Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            output.writeUTF(request.toJSON());
            System.out.printf("Sent: %s \n", request.toJSON());
            System.out.print("Received: " + input.readUTF());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
