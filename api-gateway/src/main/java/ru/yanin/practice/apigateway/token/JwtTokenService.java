package ru.yanin.practice.apigateway.token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import ru.yanin.practice.token.Token;

import java.time.Instant;
import java.util.Objects;

import static ru.yanin.practice.cookie.CookieName.HOST_AUTH_TOKEN;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer;

    @Override
    public boolean isValidToken(Token token) {
        return token.expiresAt().isAfter(Instant.now());
    }

    @Override
    public Token extractToken(ServerHttpRequest request) {
        String cookieName = HOST_AUTH_TOKEN.getName();
        HttpCookie cookie = request.getCookies().getFirst(cookieName);
        String stringToken = Objects.requireNonNull(cookie,
                        String.format("Cookie with name %s not found in request's cookies", cookieName))
                .getValue();
        return tokenCookieJweStringDeserializer.apply(stringToken);
    }
}
