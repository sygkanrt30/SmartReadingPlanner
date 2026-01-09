package ru.yanin.practice.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.practice.user_service.exception.handler.ParentException;


public class RegistrationException extends ParentException {
    public RegistrationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }

    public RegistrationException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
