package ru.yanin.practice.user_service.service.email.verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;
import ru.yanin.practice.user_service.service.email.verification.limiter.attempt.AttemptLimiter;
import ru.yanin.practice.user_service.service.email.verification.storage.CodeStorage;
import ru.yanin.shared.message_broker.producer.Producer;
import ru.yanin.practice.user_service.service.user.user_credentials.UserCredentialService;

import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserCredentialService userService;
    private final CodeStorage codeStorage;
    private final Producer<VerificationEvent> producer;
    private final AttemptLimiter attemptLimiter;

    @Override
    public void generateAndSendCode(String email, Long userId) {
        boolean isBelongToSameUser = userService.checkUserIdAndEmailBelongToSameUser(email, userId);
        if (!isBelongToSameUser) {
            throw new VerificationException("Email verification failed: userId and email dont belong to same user");
        }
        if (codeStorage.isBlocked(email)) {
            throw new VerificationException("To many request!", TOO_MANY_REQUESTS);
        }
        String code = CodeGenerator.generateCode();
        codeStorage.blockForResend(email);
        codeStorage.saveCode(code, email);
        sendToNotificationService(email, code);
        log.info("Code sent successfully");
    }

    private void sendToNotificationService(String email, String code) {
        var verificationEvent = new VerificationEvent(email, code);
        producer.send(verificationEvent);
    }

    @Override
    public boolean verifyEmail(String email, String code) {
        if (!attemptLimiter.isAttemptAllowed(email)) {
            throw new VerificationException("Attempt limit reached", TOO_MANY_REQUESTS);
        }
        Optional<String> storedCode = codeStorage.getCode(email);
        if (storedCode.isEmpty() || !code.equals(storedCode.get())) {
            attemptLimiter.registerFailedAttempt(email);
            return false;
        }
        userService.changeEmailVerificationStatus(email);
        resetStorageForThisEmail(email);
        log.info("Email verification successful");
        return true;
    }

    private void resetStorageForThisEmail(String email) {
        codeStorage.deleteCode(email);
        attemptLimiter.resetAttempts(email);
    }
}
