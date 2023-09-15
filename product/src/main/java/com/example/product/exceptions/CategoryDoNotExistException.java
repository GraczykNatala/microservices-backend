package com.example.product.exceptions;

public class CategoryDoNotExistException extends RuntimeException {
    public CategoryDoNotExistException() {
        super();
    }
    public CategoryDoNotExistException(String message) {
        super(message);
    }

    public CategoryDoNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryDoNotExistException(Throwable cause) {
        super(cause);
    }
}
