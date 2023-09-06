package com.example.auth.entity;

public enum Code {
    SUCCESS("Operacja zakończona sukcesem"),
    LOGIN_FAILED("Nie udało się zalogować"),
    USER_NOT_EXIST("Użytkownik o wskazanej nazwie nie istnieje"),
    USERNAME_ALREADY_EXIST("Użytkownik o wskazanej nazwie już istnieje"),
    EMAIL_ALREADY_EXIST("Użytkownik z tym adresem email już istnieje"),
    PERMIT("Przyznano dostęp"),
    BAD_TOKEN("Wskazany token jest pusty lub nie ważny");


    public final String label;
    private Code(String label){
        this.label = label;
    }
}
