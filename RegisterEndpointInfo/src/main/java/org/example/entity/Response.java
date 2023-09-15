package org.example.entity;

import java.sql.Timestamp;

public class Response {
    private String message;
    private String timestamp;

    public Response(String message) {
        this.message = message;
        this.timestamp =  String.valueOf(new Timestamp(System.currentTimeMillis()));;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
