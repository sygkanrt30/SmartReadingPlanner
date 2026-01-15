package ru.yanin.practice.apigateway.token;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import ru.yanin.practice.token.ClaimName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TokenCookieJweStringDeserializerTest {
    @Mock
    private JWEDecrypter jweDecrypter;

    @InjectMocks
    private TokenCookieJweStringDeserializer deserializer;

    @Test
    void apply_ShouldReturnToken_WhenValidJWEStringProvided() throws Exception {
        var jweString = "valid.jwe.token";
        var expectedJwtId = UUID.randomUUID();
        var expectedUsername = "testUser";
        var expectedRole = List.of("ROLE_USER");
        var expectedIssueTime = Instant.now().minusSeconds(300);
        var expectedExpirationTime = Instant.now().plusSeconds(3600);
        var claimsSet = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .jwtID(expectedJwtId.toString())
                .subject(expectedUsername)
                .claim(ClaimName.ROLE.getName(), expectedRole)
                .issueTime(Date.from(expectedIssueTime))
                .expirationTime(Date.from(expectedExpirationTime))
                .build();
        var encryptedJWT = mock(EncryptedJWT.class);
        when(encryptedJWT.getJWTClaimsSet()).thenReturn(claimsSet);

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(jweString))
                    .thenReturn(encryptedJWT);

            var result = deserializer.apply(jweString);

            assertNotNull(result);
            assertEquals(expectedJwtId, result.id());
            assertEquals(expectedUsername, result.username());
            assertEquals(expectedRole, result.roles());

            verify(encryptedJWT).decrypt(jweDecrypter);
        }
    }

    @Test
    void apply_ShouldReturnNull_WhenParseExceptionOccurs() {
        var invalidJweString = "invalid.jwe.token";

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(invalidJweString))
                    .thenThrow(new ParseException("Invalid JWE format", 0));

            var result = deserializer.apply(invalidJweString);

            assertNull(result);
            verifyNoInteractions(jweDecrypter);
        }
    }

    @Test
    void apply_ShouldReturnNull_WhenJOSEExceptionOccursDuringDecryption() throws Exception {
        var jweString = "encrypted.jwe.token";

        var encryptedJWT = mock(EncryptedJWT.class);
        doThrow(new JOSEException("Decryption failed")).when(encryptedJWT).decrypt(jweDecrypter);

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(jweString)).thenReturn(encryptedJWT);

            var result = deserializer.apply(jweString);

            assertNull(result);
            verify(encryptedJWT).decrypt(jweDecrypter);
        }
    }

    @Test
    void apply_ShouldHandleEmptyAuthoritiesList() throws Exception {
        var jweString = "valid.jwe.token";
        var expectedJwtId = UUID.randomUUID();
        var expectedUsername = "testUser";
        var expectedRoles = List.<String>of();
        var claimsSet = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .jwtID(expectedJwtId.toString())
                .subject(expectedUsername)
                .claim(ClaimName.ROLE.getName(), expectedRoles)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .build();
        var encryptedJWT = mock(EncryptedJWT.class);
        when(encryptedJWT.getJWTClaimsSet()).thenReturn(claimsSet);

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(jweString)).thenReturn(encryptedJWT);

            var result = deserializer.apply(jweString);

            assertNotNull(result);
            assertEquals(expectedJwtId, result.id());
            assertEquals(expectedUsername, result.username());
            assertEquals(expectedRoles, result.roles());
            assertTrue(result.roles().isEmpty());
        }
    }

    @Test
    void apply_ShouldHandleNullAuthoritiesClaim() throws Exception {
        var jweString = "valid.jwe.token";
        var expectedJwtId = UUID.randomUUID();
        var expectedUsername = "testUser";
        var claimsSet = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .jwtID(expectedJwtId.toString())
                .subject(expectedUsername)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .build();
        var encryptedJWT = mock(EncryptedJWT.class);
        when(encryptedJWT.getJWTClaimsSet()).thenReturn(claimsSet);

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(jweString)).thenReturn(encryptedJWT);

            var result = deserializer.apply(jweString);

            assertNotNull(result);
            assertEquals(expectedJwtId, result.id());
            assertEquals(expectedUsername, result.username());
            assertNull(result.roles());
        }
    }

    @Test
    void apply_ShouldReturnNull_WhenRequiredClaimsAreMissing() throws Exception {
        var jweString = "valid.jwe.token";
        var claimsSet = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issueTime(Date.from(Instant.now()))
                .build();
        var encryptedJWT = mock(EncryptedJWT.class);
        when(encryptedJWT.getJWTClaimsSet()).thenReturn(claimsSet);

        try (var encryptedJWTMock = mockStatic(EncryptedJWT.class)) {
            encryptedJWTMock.when(() -> EncryptedJWT.parse(jweString)).thenReturn(encryptedJWT);

            assertThrows(NullPointerException.class, () -> deserializer.apply(jweString));
        }
    }
}
