package ru.yanin.practice.apigateway.ratelimit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import ru.yanin.practice.apigateway.config.RateLimitConfig;
import ru.yanin.practice.apigateway.token.TokenService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitUtil {

    private final RateLimitConfig config;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final TokenService tokenService;

    public boolean isEnabled() {
        return config.isEnabled();
    }

    public boolean isExcluded(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        List<String> excludedPaths = config.getExcludedPaths();
        if (excludedPaths != null) {
            return excludedPaths.stream()
                    .anyMatch(path::matches);
        }
        return false;
    }

    public String buildRateLimitKey(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        var key = new StringBuilder();
        key.append("global:");

        if (config.isPerEndpoint()) {
            String path = request.getPath().value();
            String normalizedPath = path.replaceAll("/\\d+", "/{id}");
            key.append(":path:").append(normalizedPath);
        }

        if (config.isPerMethod()) {
            key.append(":method:").append(request.getMethod());
        }

        if (config.isPerUser()) {
            String userId = getUserId(exchange);
            key.append(":user:").append(userId);
        }

        return key.toString();
    }

    private String getUserId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String stringToken = tokenService.extractToken(request);
        try {
            var token = tokenService.validateToken(stringToken);
            return token.userId().toString();
        } catch (NullPointerException e) {
            log.error("Token missed", e);
            throw new RuntimeException(e);
        }
    }

    public boolean checkRateLimit(String key, ServerHttpRequest request) {
        long windowMillis = getWindowMillis(request);
        long windowsIndex = System.currentTimeMillis() / windowMillis;
        String redisKey = String.format("rate-limit:%s:%d", key, windowsIndex);

        long countHits = redisTemplate.opsForValue().increment(redisKey)
                .blockOptional()
                .orElse(0L);

        if (countHits == 1L) {
            redisTemplate.expire(key, Duration.of(windowMillis, ChronoUnit.MILLIS));
        }

        return countHits != 0L && countHits < config.getDefaultMaxRequests();
    }

    public int getMaxRequests(ServerHttpRequest request) {
        String path = request.getPath().value();
        Map<String, Integer> customLimits = config.getCustomLimits();
        if (customLimits != null) {
            for (var entry : customLimits.entrySet()) {
                if (path.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return config.getDefaultMaxRequests();
    }

    public long getWindowMillis(ServerHttpRequest request) {
        String path = request.getPath().value();
        Map<String, Long> customWindows = config.getCustomWindows();
        if (customWindows != null) {
            for (var entry : customWindows.entrySet()) {
                if (path.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return config.getDefaultWindowMillis();
    }
}
