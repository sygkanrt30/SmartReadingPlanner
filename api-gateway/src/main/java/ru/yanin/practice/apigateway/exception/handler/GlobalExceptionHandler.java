package ru.yanin.practice.apigateway.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yanin.practice.apigateway.exception.ExtractTokenException;
import ru.yanin.practice.exception.PropertyName;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ProblemDetail catchNullPointerException(NullPointerException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ProblemDetail catchExtractTokenException(ExtractTokenException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.BAD_REQUEST);
    }

    private ProblemDetail getAppErrorHandlerResponseDto(Exception e, HttpStatus status) {
        String error = e.getMessage();
        var problemDetail = ProblemDetail.forStatusAndDetail(status, error);
        problemDetail.setTitle(error);
        problemDetail.setProperty(PropertyName.ERROR_CODE.value(), status.getReasonPhrase());
        problemDetail.setProperty(PropertyName.TIMESTAMP.value(), Instant.now());
        log.error(error, e.getCause());
        return problemDetail;
    }
}
