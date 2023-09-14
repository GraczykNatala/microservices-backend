package com.example.product.exceptions;

public class ObjectExistsInDbException extends RuntimeException {
    public ObjectExistsInDbException(String message) {
        super(message);
    }

    public ObjectExistsInDbException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectExistsInDbException(Throwable cause) {
        super(cause);
    }
}
