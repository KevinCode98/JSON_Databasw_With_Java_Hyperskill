package server.database;

import server.exceptions.NoSuchKeyException;

import java.util.HashMap;
import java.util.Map;

public enum Database {

    INSTANCE;

    private final Map<String, String> database;

    Database() {
        database = new HashMap<>();
    }

    public void set(String key, String value) {
        database.put(key, value);
    }

    public String get(String key) {
        if (database.containsKey(key)) {
            return database.get(key);
        }
        throw new NoSuchKeyException();
    }

    public void delete(String key) {
        if (!database.containsKey(key)) {
            throw new NoSuchKeyException();
        }
        database.remove(key);
    }

}
