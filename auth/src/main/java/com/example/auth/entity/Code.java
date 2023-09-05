package com.example.auth.entity;

public enum Code {
    SUCCESS("Operacja zakończona sukcesem");


    public final String label;
    private Code(String label){
        this.label = label;
    }
}
