package ru.yanin.practice.apigateway.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class InvalidTokenException extends SmartReadingPlannerException {
  public InvalidTokenException(String message) {
    super(message, HttpStatus.UNAUTHORIZED);
  }

  public InvalidTokenException(String message, Throwable cause) {
    super(message, cause, HttpStatus.UNAUTHORIZED);
  }
}
