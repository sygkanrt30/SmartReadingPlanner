package ru.yanin.practice.user_service.exception.handler;

import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import ru.yanin.shared.exception.SmartReadingPlannerException;
import ru.yanin.shared.exception.PropertyName;

import java.time.Instant;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class, NotFoundException.class})
    public ProblemDetail catchNotFoundException(Exception e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ProblemDetail catchIllegalArgumentException(IllegalArgumentException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ProblemDetail catchCustomException(SmartReadingPlannerException e) {
        return getAppErrorHandlerResponseDto(e, e.responseStatus());
    }

    @ExceptionHandler
    public ProblemDetail catchResponseException(ResponseStatusException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.resolve(e.getStatusCode().value()));
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
