package ru.yanin.practice.user_service.service.email.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;
import ru.yanin.practice.user_service.service.email.verification.limiter.attempt.AttemptLimiter;
import ru.yanin.practice.user_service.service.email.verification.storage.CodeStorage;
import ru.yanin.practice.user_service.service.user.user_credentials.UserCredentialService;
import ru.yanin.shared.message_broker.producer.Producer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private UserCredentialService userCredentialService;

    @Mock
    private CodeStorage codeStorage;

    @Mock
    private Producer<VerificationEvent> producer;

    @Mock
    private AttemptLimiter attemptLimiter;

    @Captor
    private ArgumentCaptor<VerificationEvent> verificationEventCaptor;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Test
    void generateAndSendCode_ShouldSendEventToQueueAndSaveCodeInStorage_WhenEmailAndUserIdValid() {
        String code = "765754";
        String email = "test@test.com";
        Long userId = 11L;
        MockedStatic<CodeGenerator> codeGeneratorMockStatic = mockStatic(CodeGenerator.class);
        when(userCredentialService.checkUserIdAndEmailBelongToSameUser(anyString(), anyLong()))
                .thenReturn(true);
        codeGeneratorMockStatic.when(CodeGenerator::generateCode).thenReturn(code);

        emailVerificationService.generateAndSendCode(email, userId);

        verify(codeStorage).saveCode(eq(code), eq(email));
        verify(producer).send(verificationEventCaptor.capture());
        assertEquals(code, verificationEventCaptor.getValue().code());
        codeGeneratorMockStatic.close();
    }

    @Test
    void generateAndSendCode_ShouldThrowException_WhenEmailAndUserIdInvalid() {
        String email = "test@test.com";
        Long userId = 11L;
        when(userCredentialService.checkUserIdAndEmailBelongToSameUser(anyString(), anyLong()))
                .thenReturn(false);

        assertThrows(VerificationException.class, () -> emailVerificationService.generateAndSendCode(email, userId));
        verify(codeStorage, never()).saveCode(anyString(), eq(email));
        verify(producer, never()).send(any());
    }

    @Test
    void generateAndSendCode_ShouldThrowException_WhenUserHasBlockToGenerateAndSendCode() {
        String email = "test@test.com";
        Long userId = 11L;
        // isBlocked(email) return true

        assertThrows(VerificationException.class, () -> emailVerificationService.generateAndSendCode(email, userId));
        verify(codeStorage, never()).saveCode(anyString(), eq(email));
        verify(producer, never()).send(any());
    }

    @Test
    void verifyEmail_ShouldChangeEmailStatus_WhenCodeAndEmailValid() {
        String code = "765754";
        String email = "test@test.com";
        when(attemptLimiter.isAttemptAllowed(email)).thenReturn(true);
        when(codeStorage.getCode(anyString())).thenReturn(Optional.of(code));

        boolean result = emailVerificationService.verifyEmail(email, code);

        assertTrue(result);
        verify(codeStorage).deleteCode(eq(email));
        verify(userCredentialService).changeEmailVerificationStatus(eq(email));
    }

    @Test
    void verifyEmail_ShouldReturnFalse_WhenEmailInvalid() {
        String code = "765754";
        String email = "test@test.com";
        when(attemptLimiter.isAttemptAllowed(email)).thenReturn(true);
        when(codeStorage.getCode(email)).thenReturn(Optional.empty());

        boolean result = emailVerificationService.verifyEmail(email, code);

        assertFalse(result);
        verify(attemptLimiter).registerFailedAttempt(email);
    }

    @Test
    void verifyEmail_ShouldReturnFalse_WhenCodeInvalid() {
        String storedCode = "765754";
        String argCode = "765744";
        String email = "test@test.com";
        when(attemptLimiter.isAttemptAllowed(email)).thenReturn(true);
        when(codeStorage.getCode(email)).thenReturn(Optional.of(storedCode));

        boolean result = emailVerificationService.verifyEmail(email, argCode);

        assertFalse(result);
        verify(attemptLimiter).registerFailedAttempt(email);
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenAttemptIsNotAllowed() {
        String code = "765754";
        String email = "test@test.com";
        when(attemptLimiter.isAttemptAllowed(email)).thenReturn(false);

        var thrown = assertThrows(VerificationException.class,
                () -> emailVerificationService.verifyEmail(email, code));

        assertEquals("Attempt limit reached", thrown.getMessage());
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, thrown.responseStatus());
    }
}