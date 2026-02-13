package ru.yanin.practise.bookservice.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.ParentException;

public class BookNotFoundInApiException extends ParentException {

    public BookNotFoundInApiException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
