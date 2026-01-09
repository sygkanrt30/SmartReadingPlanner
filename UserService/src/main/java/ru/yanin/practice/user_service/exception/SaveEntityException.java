package ru.yanin.practice.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.practice.user_service.exception.handler.ParentException;

public class SaveEntityException extends ParentException {
    public SaveEntityException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }

    public SaveEntityException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
