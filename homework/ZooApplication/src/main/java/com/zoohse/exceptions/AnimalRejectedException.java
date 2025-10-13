package com.zoohse.exceptions;

public class AnimalRejectedException extends Exception {
    public AnimalRejectedException(String s) {
        super(s);
    }

    public AnimalRejectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
