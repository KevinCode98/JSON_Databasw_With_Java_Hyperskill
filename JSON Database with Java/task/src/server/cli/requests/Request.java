package server.cli.requests;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    @Parameter(names = {"-t", "--type"}, description = "The type of the request")
    private String type;

    @Parameter(names = {"-k", "--key"}, description = "The Record key")
    private String key;

    @Parameter(names = {"-v", "--value"}, description = "The text value to add")
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toJSON() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("type", type);
        if (key != null) {
            map.put("key", key);
        }
        if (value != null) {
            map.put("value", value);
        }
        return new Gson().toJson(map);
    }
}
