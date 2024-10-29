package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the server for handling client requests.
 * */

public class Main {
    private static Database database;
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        String dbFilePath = System.getProperty("user.dir") + "/src/server/data/db.json";
        database = new Database(dbFilePath);

        int port = 1024;
        ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"));
        System.out.println("Server started!");

        while (true) {
            Socket socket = server.accept();
            ClientHandler clientHandler = new ClientHandler(socket, server);
            clients.add(clientHandler);

            clientHandler.start();

            if(server.isClosed()) {
                for(ClientHandler client : clients) {
                    client.join();
                }
                break;
            }
        }
    }

    /**
     * This class is responsible for handling client connections.
     * Each instance of ClientHandler manages a single client connection.
     * */
    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final ServerSocket server;

        private ClientHandler(Socket socket, ServerSocket server) {
            this.socket = socket;
            this.server = server;
        }

        public void run() {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                String received = in.readUTF();
                System.out.println("Received: " + received);

                String sent = database.executeCommand(received);
                out.writeUTF(sent);
                System.out.println("Sent: " + sent);

                if (received.contains("\"type\":\"exit\"")) {
                    in.close();
                    out.close();
                    socket.close();
                    server.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}