package ru.yanin.practice.user_service.service.email.verification;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAcceptableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;
import ru.yanin.practice.user_service.service.email.verification.storage.CodeStorageService;
import ru.yanin.practice.user_service.service.rabbitMq.Producer;
import ru.yanin.practice.user_service.service.user.UserService;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final UserService userService;
    private final CodeStorageService codeStorageService;
    private final Producer<VerificationEvent> producer;


    @Override
    public void sendCode(String email, Long userId) {
        boolean isBelongToSameUser = userService.checkUserIdAndEmailBelongToSameUser(email, userId);
        if (!isBelongToSameUser) {
            throw new NotAcceptableException("Email verification failed");
        }
        String code = generateCode();
        codeStorageService.saveCode(code, email);
        var verificationEvent = new VerificationEvent(email, code);
        producer.send(verificationEvent);
        log.info("Code sent successfully");
    }

    private String generateCode() {
        var random = new Random();
        int firstCodePart = 100 + random.nextInt(899);
        int secondCodePart = 100 + random.nextInt(899);
        return String.format("%d%d", firstCodePart, secondCodePart);
    }

    @Override
    public void verifyEmail(String email, String code) {
        String storedCode = codeStorageService.getCode(email)
                .orElseThrow(() -> new BadRequestException("The code is out of date or not found"));

        if (!storedCode.equals(code)) {
            throw new BadRequestException("Invalid code");
        }
        codeStorageService.deleteCode(email);
        userService.changeEmailVerificationStatus(email);
        log.info("Email verification successful");
    }
}
