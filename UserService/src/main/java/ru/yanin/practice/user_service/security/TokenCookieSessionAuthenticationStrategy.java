package ru.yanin.practice.user_service.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import ru.yanin.practice.cookie.CookieName;
import ru.yanin.practice.token.Token;
import ru.yanin.practice.user_service.service.token.util.TokenCookieFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Setter
@Slf4j
public class TokenCookieSessionAuthenticationStrategy implements SessionAuthenticationStrategy {

    private Function<Authentication, Token> tokenCookieFactory = new TokenCookieFactory();
    private Function<Token, String> tokenStringSerializer;

    @Override
    public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) throws SessionAuthenticationException {

        var token = tokenCookieFactory.apply(authentication);
        var tokenString = tokenStringSerializer.apply(token);

        var cookie = new Cookie(CookieName.HOST_AUTH_TOKEN.name(), tokenString);
        cookie.setPath("/");
        cookie.setDomain(null);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));
        log.debug("Send cookie for user with username {}", authentication.getName());
        response.addCookie(cookie);
    }
}
