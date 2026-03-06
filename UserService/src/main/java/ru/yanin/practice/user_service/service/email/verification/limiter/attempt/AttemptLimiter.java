package ru.yanin.practice.user_service.service.email.verification.limiter.attempt;

public interface AttemptLimiter {

    boolean isAttemptAllowed(String email);

    void registerFailedAttempt(String email);

    void resetAttempts(String email);

    int getAttemptCount(String email);
}
