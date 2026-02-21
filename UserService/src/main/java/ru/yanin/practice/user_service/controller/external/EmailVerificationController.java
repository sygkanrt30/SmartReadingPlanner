package ru.yanin.practice.user_service.controller.external;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yanin.practice.user_service.service.email.verification.EmailVerificationService;

@RestController
@RequestMapping("${spring.application.base-url}/email")
@RequiredArgsConstructor
class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendCode(@RequestParam String email,
                                      @RequestHeader("X-User-ID") Long userId) {
        
        emailVerificationService.sendCode(email, userId);
        return ResponseEntity.ok("Код отправлен на " + email);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String email,
                                         @RequestParam String code) {

        emailVerificationService.verifyEmail(email, code);
        return ResponseEntity.ok("Email подтвержден!");
    }
}
