package ru.yanin.practice.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class SaveEntityException extends SmartReadingPlannerException {
    public SaveEntityException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }

    public SaveEntityException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
