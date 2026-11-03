package ru.yanin.shared.exception;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Accessors(fluent = true)
public class SmartReadingPlannerException extends RuntimeException {

    private final HttpStatus responseStatus;

    protected SmartReadingPlannerException(String message, Throwable cause, HttpStatus responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }

    protected SmartReadingPlannerException(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
}
