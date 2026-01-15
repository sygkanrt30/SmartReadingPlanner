package ru.yanin.practice.user_service.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.yanin.practice.user_service.exception.RegistrationException;
import ru.yanin.practice.user_service.model.entity.UserCredentials;
import ru.yanin.practice.user_service.repository.UserCredentialsRepository;
import ru.yanin.practice.user_service.security.TokenCookieSessionAuthenticationStrategy;

@RequiredArgsConstructor
@Service
@Slf4j
public class Authenticator {

    private final TokenCookieSessionAuthenticationStrategy tokenCookieSessionAuthenticationStrategy;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public void authenticateAndSetCookie(HttpServletRequest request, HttpServletResponse response,
                                         String username, byte[] password) {
        log.trace("trying to authenticate user with username ({}) after registration", username);
        try {
            UserCredentials userCredentials = userCredentialsRepository.findByUsername(username).orElseThrow();

            String rawPassword = new String(password);
            if (!passwordEncoder.matches(rawPassword, userCredentials.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    userCredentials,
                    userCredentials.userId(),
                    userCredentials.getAuthorities()
            );
            tokenCookieSessionAuthenticationStrategy.onAuthentication(authentication, request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RegistrationException("Authentication failed after registration", e);
        }
        log.debug("successfully authenticated user with username ({}) after registration", username);
    }
}
