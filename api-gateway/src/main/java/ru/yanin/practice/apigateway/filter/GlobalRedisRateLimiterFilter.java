package ru.yanin.practice.apigateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yanin.practice.apigateway.ratelimit.util.RateLimitUtil;

import java.time.Instant;
import java.util.LinkedHashMap;

import static ru.yanin.practice.header.HeaderName.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalRedisRateLimiterFilter implements GlobalFilter, Ordered {

    private static final int MIDDLE_PRECEDENCE = 0;

    private final RateLimitUtil rateLimitUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimitUtil.isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        if (rateLimitUtil.isExcluded(request)) {
            return chain.filter(exchange);
        }

        return rateLimitUtil.checkRateLimit(request)
                .flatMap(isAllowed -> {
                    if (isAllowed) {
                        return chain.filter(exchange);
                    }
                    return buildRateLimitResponse(exchange);

                })
                .onErrorResume(e -> {
                    log.error("Rate limit check failed", e);
                    return chain.filter(exchange);
                });
    }

    private Mono<Void> buildRateLimitResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        addRateLimitHeaders(exchange);
        var responseBody = new LinkedHashMap<String, Object>();
        responseBody.put("timestamp", Instant.now().toString());
        responseBody.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        responseBody.put("error", "Too Many Requests");
        responseBody.put("message", "Rate limit exceeded");

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(responseBody);
            DataBuffer buffer = response.bufferFactory()
                    .wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    private void addRateLimitHeaders(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(RATE_LIMIT_POLICY.value(),
                rateLimitUtil.getMaxRequests(exchange.getRequest()) + ";" +
                        rateLimitUtil.getWindowMillis(exchange.getRequest()) / 1000 + "s");
    }

    @Override
    public int getOrder() {
        return MIDDLE_PRECEDENCE;
    }
}
