package ru.yanin.practice.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class EntityNotFoundException extends SmartReadingPlannerException {
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public EntityNotFoundException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
