package com.example.product.entity;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class BasicResponse {
    private final String timestamp;
    private final String message;

    public BasicResponse(String message) {
        this.timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        this.message = message;
    }
}
