package ru.yanin.practise.bookservice.exception;

import org.springframework.http.HttpStatus;
import ru.yanin.shared.exception.SmartReadingPlannerException;

public class FailedLoadFileException extends SmartReadingPlannerException {

  public FailedLoadFileException(String message, Throwable cause) {
    super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
