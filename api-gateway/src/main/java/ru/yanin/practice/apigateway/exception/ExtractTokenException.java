package ru.yanin.practice.apigateway.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class ExtractTokenException extends SmartReadingPlannerException {
    public ExtractTokenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ExtractTokenException(String message, Throwable cause) {
        super(message, cause, HttpStatus.FORBIDDEN);
    }
}
