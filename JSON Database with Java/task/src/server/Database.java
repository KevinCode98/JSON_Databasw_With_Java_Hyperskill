package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is the database this project.
 * */
public class Database {
    private final Gson gson;
    private final String dbFile;
    private final ReentrantReadWriteLock lock;

    public Database(String dbFile) {
        this.lock = new ReentrantReadWriteLock();
        this.dbFile = dbFile;
        this.gson = new Gson();
    }

    /**
     * This method executes the user's commands.
     * */
    public String executeCommand(String jsonString) {
        try {
            JsonObject json = gson.fromJson(jsonString, JsonObject.class);
            String type = json.get("type").getAsString();

            JsonElement keyElement = json.get("key");
            JsonArray keyArray = new JsonArray();

            if (keyElement != null) {
                if (keyElement.isJsonPrimitive()) {
                    keyArray.add(keyElement.getAsString());
                } else if (keyElement.isJsonArray()) {
                    keyArray = keyElement.getAsJsonArray();
                }
            }

            JsonElement value = json.get("value");

            return switch (type) {
                case "set" -> set(keyArray, value);
                case "get" -> get(keyArray);
                case "delete" -> delete(keyArray);
                case "exit" -> createResponse();
                default -> createErrorResponse("Invalid command");
            };
        } catch (IllegalStateException e) {
            return createErrorResponse("Invalid JSON");
        }
    }

    /**
     * This method sets the user's inputFile.json in the specified json key.
     * */
    private String set(JsonArray key, JsonElement value) {
        lock.writeLock().lock();
        try {
            JsonObject database = readDatabase();
            JsonObject targetObj = getTargetObj(database, key);
            String finalKey = key.get(key.size() - 1).getAsString();
            if (targetObj != null) {
                targetObj.add(finalKey, value);
            }
            writeDatabase(database);
            return createResponse();
        } catch (IOException e) {
            return createErrorResponse("Failed to write to database");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * This method gets the value from the specified json key.
     * */
    private String get(JsonArray key) {
        lock.readLock().lock();
        try {
            JsonObject database = readDatabase();
            JsonObject targetObj = getTargetObj(database, key);

            if (targetObj != null) {
                JsonElement finalValue = targetObj.get(key.get(key.size() - 1).getAsString());
                if (finalValue != null) {
                    JsonObject response = new JsonObject();
                    response.addProperty("response", "OK");
                    response.add("value", finalValue);
                    return gson.toJson(response);
                }
            }
            return createErrorResponse("No such key");
        } catch (IOException e) {
            return createErrorResponse("Failed to read from database");
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * This method removes the value from the specified json key.
     * */
    private String delete(JsonArray key) {
        lock.writeLock().lock();
        try {
            JsonObject database = readDatabase();
            JsonObject targetObj = getTargetObj(database, key);

            if (targetObj != null) {
                JsonElement finalValue = targetObj.get(key.get(key.size() - 1).getAsString());
                if (finalValue != null) {
                    String finalKey = key.get(key.size() - 1).getAsString();
                    targetObj.remove(finalKey);
                    writeDatabase(database);
                    return createResponse();
                }
            }
            return createErrorResponse("No such key");
        } catch (IOException e) {
            return createErrorResponse("Failed to write to database");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * This method ensures the existence of e nested path in the json file.
     * */
    private JsonObject getTargetObj(JsonObject database, JsonArray key) {
        JsonObject currentDatabase = database;

        for (int i = 0; i < key.size() - 1; i++) {
            String keyPart = key.get(i).getAsString();

            if (!currentDatabase.has(keyPart)) {
                JsonObject newObj = new JsonObject();
                currentDatabase.add(keyPart, newObj);
                currentDatabase = newObj;
            } else {
                JsonElement element = currentDatabase.get(keyPart);
                if (element.isJsonObject()) {
                    currentDatabase = element.getAsJsonObject();
                } else {
                    return null;
                }
            }
        }
        return currentDatabase;
    }

    /**
     * This method saves the data to a file (to disk).
     * */
    private void writeDatabase(JsonObject database) throws IOException {
        try (FileWriter fileWriter = new FileWriter(dbFile)) {
            gson.toJson(database, fileWriter);
        }
    }

    /**
     * This method reads the data from the file and converts it into a json object.
     * */
    private JsonObject readDatabase() throws IOException {
        lock.readLock().lock();
        try (JsonReader reader = new JsonReader(new FileReader(dbFile))) {
            return gson.fromJson(reader, JsonObject.class);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * This method creates a response in json format.
     * */
    private String createResponse() {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("response", "OK");
        return gson.toJson(jsonResponse);
    }

    /**
     * This method creates an error response in json format.
     * */
    private String createErrorResponse(String reason) {
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("response", "ERROR");
        errorResponse.addProperty("reason", reason);
        return gson.toJson(errorResponse);
    }
}
