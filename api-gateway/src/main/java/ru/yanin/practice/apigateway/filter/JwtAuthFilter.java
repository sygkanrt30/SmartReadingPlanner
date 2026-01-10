package ru.yanin.practice.apigateway.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yanin.practice.apigateway.token.TokenService;
import ru.yanin.practice.token.Token;

import java.nio.charset.StandardCharsets;

import static ru.yanin.practice.apigateway.filter.HeaderName.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements GatewayFilterFactory<JwtAuthFilter.Config> {

    private final TokenService jwtService;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String token = jwtService.extractToken(request);
            if (config.isRequired()) {
                return unauthorized(exchange, "Missing token");
            }

            try {
                Token tokenDto = jwtService.validateToken(token);

                ServerHttpRequest modifiedRequest = request.mutate()
                        .header(USER_ID.value(), tokenDto.userId().toString())
                        .header(USER_ROLES.value(), tokenDto.roles().toString())
                        .header(USER_USERNAME.value(), tokenDto.username())
                        .build();
                logTokenIfLogLevelIsDebug(tokenDto, request);
                return chain.filter(
                        exchange.mutate().request(modifiedRequest).build()
                );
            } catch (Exception e) {
                log.warn("Invalid token: {}", e.getMessage(), e);
                return unauthorized(exchange, "Invalid token");
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    private void logTokenIfLogLevelIsDebug(Token tokenDto, ServerHttpRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("User {} accessed {}",
                    tokenDto.username(),
                    request.getURI());
        }
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Config {
        boolean required = true;
    }
}
