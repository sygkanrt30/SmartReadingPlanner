package ru.yanin.practice.apigateway.token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import ru.yanin.practice.token.Token;

import java.util.Objects;

import static ru.yanin.practice.cookie.CookieName.HOST_AUTH_TOKEN;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer;

    @Override
    public Token validateToken(String token) {
        return tokenCookieJweStringDeserializer.apply(token);
    }

    @Override
    public String extractToken(ServerHttpRequest request) {
        return Objects.requireNonNull(request.getCookies()
                .getFirst(HOST_AUTH_TOKEN.getName()))
                .getValue();
    }
}
