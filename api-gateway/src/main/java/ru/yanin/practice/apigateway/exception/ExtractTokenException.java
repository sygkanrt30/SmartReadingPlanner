package ru.yanin.practice.apigateway.exception;

public class ExtractTokenException extends RuntimeException {
    public ExtractTokenException(String message) {
        super(message);
    }

    public ExtractTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
