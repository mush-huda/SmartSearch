package com.huda.exception;

public class ApiKeyMissingException extends RuntimeException {

    public ApiKeyMissingException() {
        super("Missing API Key");
    }
}
