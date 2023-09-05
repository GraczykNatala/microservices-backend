package com.example.auth.entity;

public enum Code {
    SUCCESS("Operation end successfully");

    public final String label;
    private Code(String label) {
        this.label = label;
    }
}
