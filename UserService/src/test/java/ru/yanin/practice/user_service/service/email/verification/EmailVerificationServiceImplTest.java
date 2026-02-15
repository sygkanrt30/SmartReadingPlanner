package ru.yanin.practice.user_service.service.email.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;
import ru.yanin.practice.user_service.service.email.verification.storage.CodeStorageService;
import ru.yanin.shared.message_broker.producer.Producer;
import ru.yanin.practice.user_service.service.user.user_credentials.UserCredentialService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceImplTest {

    @Mock
    private UserCredentialService userCredentialService;

    @Mock
    private CodeStorageService codeStorageService;

    @Mock
    private Producer<VerificationEvent> producer;

    @Captor
    private ArgumentCaptor<VerificationEvent> verificationEventCaptor;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    @Test
    void sendCode_ShouldSendEventToQueueAndSaveCodeInStorage_WhenEmailAndUserIdValid() {
        String code = "765754";
        String email = "test@test.com";
        Long userId = 11L;
        MockedStatic<CodeGenerator> codeGeneratorMockStatic = mockStatic(CodeGenerator.class);
        when(userCredentialService.checkUserIdAndEmailBelongToSameUser(anyString(), anyLong()))
                .thenReturn(true);
        codeGeneratorMockStatic.when(CodeGenerator::generateCode).thenReturn(code);

        emailVerificationService.sendCode(email, userId);

        verify(codeStorageService).saveCode(eq(code), eq(email));
        verify(producer).send(verificationEventCaptor.capture());
        assertEquals(code, verificationEventCaptor.getValue().code());
        codeGeneratorMockStatic.close();
    }

    @Test
    void sendCode_ShouldThrowException_WhenEmailAndUserIdInvalid() {
        String email = "test@test.com";
        Long userId = 11L;
        when(userCredentialService.checkUserIdAndEmailBelongToSameUser(anyString(), anyLong()))
                .thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> emailVerificationService.sendCode(email, userId));
        verify(codeStorageService, never()).saveCode(anyString(), eq(email));
        verify(producer, never()).send(any());
    }

    @Test
    void verifyEmail_ShouldChangeEmailStatus_WhenCodeAndEmailValid() {
        String code = "765754";
        String email = "test@test.com";
        when(codeStorageService.getCode(anyString())).thenReturn(Optional.of(code));

        emailVerificationService.verifyEmail(email, code);

        verify(codeStorageService).deleteCode(eq(email));
        verify(userCredentialService).changeEmailVerificationStatus(eq(email));
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenEmailInvalid() {
        String code = "765754";
        String email = "test@test.com";
        when(codeStorageService.getCode(email)).thenReturn(Optional.empty());

        var thrown = assertThrows(ResponseStatusException.class,
                () -> emailVerificationService.verifyEmail(email, code));

        assertEquals("The code is out of date or not found", thrown.getReason());
        verify(codeStorageService, never()).deleteCode(anyString());
    }

    @Test
    void verifyEmail_ShouldThrowException_WhenCodeInvalid() {
        String storedCode = "765754";
        String argCode = "765744";
        String email = "test@test.com";
        when(codeStorageService.getCode(email)).thenReturn(Optional.of(storedCode));

        var thrown = assertThrows(ResponseStatusException.class,
                () -> emailVerificationService.verifyEmail(email, argCode));

        assertEquals("Invalid code", thrown.getReason());
        verify(codeStorageService, never()).deleteCode(anyString());
    }
}