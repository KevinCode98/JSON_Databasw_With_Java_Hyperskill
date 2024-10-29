package server;

import com.google.gson.Gson;
import server.cli.CommandExecutor;
import server.cli.commands.DeleteCommand;
import server.cli.commands.GetCommand;
import server.cli.commands.SetCommand;
import server.cli.requests.Request;
import server.cli.requests.Response;
import server.exceptions.NoSuchRequestException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) throws IOException {

        //Design pattern here!
        final CommandExecutor executor = new CommandExecutor();

        ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS));
        System.out.println("Server started!");

        while(true){
            try(Socket socket = server.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream()))
            {
                Request request =  new Gson().fromJson(input.readUTF(), Request.class);
                Response response = new Response();

                try {
                    switch (request.getType()) {
                        case "get":
                            GetCommand getCmd = new GetCommand(request.getKey());
                            executor.executeCommand(getCmd);
                            response.setValue(getCmd.getResult());
                            break;
                        case "set":
                            SetCommand setCmd = new SetCommand(request.getKey(), request.getValue());
                            executor.executeCommand(setCmd);
                            break;
                        case "delete":
                            DeleteCommand deleteCmd = new DeleteCommand(request.getKey());
                            executor.executeCommand(deleteCmd);
                            break;
                        case "exit":
                            response.setResponse(Response.STATUS_OK);
                            output.writeUTF(response.toJSON());
                            socket.close();
                            return;
                        default:
                            throw new NoSuchRequestException();
                    }
                    response.setResponse(Response.STATUS_OK);

                } catch (Exception e) {
                    response.setResponse(Response.STATUS_ERROR);
                    response.setReason(e.getMessage());
                }

                output.writeUTF(response.toJSON());

            }

            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
