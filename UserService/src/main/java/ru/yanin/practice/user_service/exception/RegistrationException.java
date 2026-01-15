package ru.yanin.practice.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.practice.user_service.exception.handler.ParentException;


public class RegistrationException extends ParentException {
    public RegistrationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public RegistrationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
