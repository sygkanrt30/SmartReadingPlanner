package ru.yanin.practice.apigateway.token;

import org.springframework.http.server.reactive.ServerHttpRequest;
import ru.yanin.shared.token.Token;

public interface TokenService {

    boolean isValidToken(Token token);

    Token extractToken(ServerHttpRequest request);
}
