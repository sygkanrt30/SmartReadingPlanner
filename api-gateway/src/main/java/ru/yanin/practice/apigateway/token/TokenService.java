package ru.yanin.practice.apigateway.token;

import org.springframework.http.server.reactive.ServerHttpRequest;
import ru.yanin.practice.token.Token;

public interface TokenService {

    Token validateToken(String token);

    String extractToken(ServerHttpRequest request);
}
