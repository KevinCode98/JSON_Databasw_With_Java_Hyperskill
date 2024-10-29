package client;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class handles command-line arguments for client operations.
 * */
public class CommandLineArgs {

    /**
     * Parameter annotations are used to map command-line arguments to the fields in this class.
     * */

    @Parameter(names = {"-t"}, description = "Type of the request")
    private String type;

    @Parameter(names = {"-k"}, description = "Specifies the key")
    private String key;

    @Parameter(names = {"-v"}, description = "Value for set")
    private String value;

    @Parameter(names = "-in", description = "Input file with the request")
    public String inputFile;

    private final Gson gson = new Gson();

    /**
     * This method builds a JSON string command based on the command-line arguments.
     * */
    public String buildCommand() {
        if (inputFile != null) {
            return readFromFile(inputFile);
        } else {
            System.out.println("null input file");
            JsonObject json = new JsonObject();
            json.addProperty("type", type);

            if (!type.equals("exit") && key != null) {
                json.addProperty("key", key);
            }

            if (type.equals("set") && value != null) {
                json.addProperty("value", value);
            }

            return gson.toJson(json);
        }
    }

    /**
     * This method reads the content of the specified file and returns it as a string.
     * */
    private String readFromFile (String fileName) {
        String path = System.getProperty("user.dir") + "/src/client/data/" + fileName;
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Failed to read from inputFile.json file");
            return null;
        }
    }
}
