package ru.yanin.practise.bookservice.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class BookNotFoundInApiException extends SmartReadingPlannerException {

    public BookNotFoundInApiException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
