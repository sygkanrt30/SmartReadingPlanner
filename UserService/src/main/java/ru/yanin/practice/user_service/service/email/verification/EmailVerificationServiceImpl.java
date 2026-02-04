package ru.yanin.practice.user_service.service.email.verification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;
import ru.yanin.practice.user_service.service.email.verification.storage.CodeStorageService;
import ru.yanin.practice.user_service.service.rabbitMq.Producer;
import ru.yanin.practice.user_service.service.user.user_credentials.UserCredentialService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserCredentialService userService;
    private final CodeStorageService codeStorageService;
    private final Producer<VerificationEvent> producer;


    @Override
    public void sendCode(String email, Long userId) {
        boolean isBelongToSameUser = userService.checkUserIdAndEmailBelongToSameUser(email, userId);
        if (!isBelongToSameUser) {
            throw new ResponseStatusException(NOT_ACCEPTABLE,"Email verification failed");
        }
        String code = CodeGenerator.generateCode();
        codeStorageService.saveCode(code, email);
        var verificationEvent = new VerificationEvent(email, code);
        producer.send(verificationEvent);
        log.info("Code sent successfully");
    }

    @Override
    public void verifyEmail(String email, String code) {
        String storedCode = codeStorageService.getCode(email)
                .orElseThrow(() -> new ResponseStatusException(
                        BAD_REQUEST,
                        "The code is out of date or not found"
                ));

        if (!storedCode.equals(code)) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid code");
        }
        codeStorageService.deleteCode(email);
        userService.changeEmailVerificationStatus(email);
        log.info("Email verification successful");
    }
}
