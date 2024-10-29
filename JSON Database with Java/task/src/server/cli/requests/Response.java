package server.cli.requests;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class Response {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ERROR = "ERROR";


    private String response;
    private String reason;
    private String value;


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toJSON() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("response", response);
        if (value != null) {
            map.put("value", value);
        }
        if (reason != null) {
            map.put("reason", reason);
        }
        return new Gson().toJson(map);
    }
}
