package ru.yanin.practice.user_service.exception.handler;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Accessors(fluent = true)
public class ParentException extends RuntimeException {

    private final HttpStatus responseStatus;

    protected ParentException(String message, Throwable cause, HttpStatus responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }

    protected ParentException(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
}
