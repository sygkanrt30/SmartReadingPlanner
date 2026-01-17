package ru.yanin.practice.user_service.exception;

public class UpdateEntityException extends SaveEntityException {

    public UpdateEntityException(String message) {
        super(message);
    }

    public UpdateEntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
