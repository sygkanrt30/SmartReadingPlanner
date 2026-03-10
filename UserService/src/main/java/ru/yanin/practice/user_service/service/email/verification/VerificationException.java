package ru.yanin.practice.user_service.service.email.verification;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class VerificationException extends SmartReadingPlannerException {

    public VerificationException(String message) {
        super(message, HttpStatus.NOT_ACCEPTABLE);
    }

    public VerificationException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
