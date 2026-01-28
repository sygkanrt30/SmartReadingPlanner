package ru.yanin.practice.apigateway.ratelimit.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.yanin.practice.apigateway.config.RateLimitConfig;
import ru.yanin.practice.apigateway.exception.ExtractTokenException;
import ru.yanin.practice.apigateway.token.TokenService;
import ru.yanin.shared.token.Token;

import java.time.Duration;
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

    public boolean isExcluded(ServerHttpRequest request) {
        String path = request.getPath().value();
        return config.getExcludedPaths().stream()
                .anyMatch(path::matches);
    }


    /**
     * Проверка превышения лимита запросов
     * true - запрос не превышает лимит
     * false - запрос превышает лимит
     */
    public Mono<Boolean> checkRateLimit(ServerHttpRequest request) {
        long windowMillis = getWindowMillis(request);
        long windowsIndex = System.currentTimeMillis() / windowMillis;

        return buildRateLimitKey(request, isAuthRequest(request))
                .flatMap(key -> {
                    String redisKey = String.format("rate-limit:%s:%d", key, windowsIndex);

                    return redisTemplate.opsForValue().increment(redisKey)
                            .flatMap(countHits -> {
                                if (countHits == 1L) {
                                    return redisTemplate.expire(redisKey, Duration.ofMillis(windowMillis))
                                            .thenReturn(countHits);
                                }
                                return Mono.just(countHits);
                            })
                            .defaultIfEmpty(0L);
                })
                .map(countHits -> isAllowed(request, countHits))
                .onErrorResume(e -> {
                    log.error("Error checking rate limit", e);
                    return Mono.just(true);
                });
    }

    private boolean isAuthRequest(ServerHttpRequest request) {
        String path = request.getPath().value();
        return path.startsWith(config.getAuthPath());
    }

    private Mono<String> buildRateLimitKey(ServerHttpRequest request, boolean isAuthRequest) {
        return Mono.defer(() -> {
            var key = new StringBuilder().append("global");

            if (config.isPerEndpoint()) {
                String path = request.getPath().value();
                String normalizedPath = path.replaceAll("/\\d+", "/{id}");
                key.append(":path:").append(normalizedPath);
            }

            if (config.isPerMethod()) {
                key.append(":method:").append(request.getMethod());
            }

            if (config.isPerUser()) {
                if (!isAuthRequest) {
                    return extractUserIdFromToken(request)
                            .map(userId -> key.append(":user:").append(userId).toString())
                            .onErrorResume(e -> {
                                log.warn("Could not extract user from token, using IP: {}", e.getMessage());
                                return Mono.just(key.append(":ip:").append(getClientIp(request)).toString());
                            });
                }
                key.append(":ip:").append(getClientIp(request));
            }
            return Mono.just(key.toString());
        });
    }

    private String getClientIp(ServerHttpRequest request) {
        if (request.getRemoteAddress() != null && request.getRemoteAddress().getAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    private Mono<String> extractUserIdFromToken(ServerHttpRequest request) {
        return Mono.fromCallable(() -> {
            Token token = tokenService.extractToken(request);
            return token.userId().toString();
        }).onErrorResume(e -> Mono.error(new ExtractTokenException("Failed to extract user_id from token", e)));
    }

    private boolean isAllowed(ServerHttpRequest request, Long countHits) {
        return countHits > 0L && countHits <= getMaxRequests(request);
    }

    public long getMaxRequests(ServerHttpRequest request) {
        Long result = getCustomPathSetting(request, config.getCustomLimits());
        if (result != null) {
            return result;
        }
        return config.getDefaultMaxRequests();
    }

    private Long getCustomPathSetting(ServerHttpRequest request, Map<String, Long> customPathSettings) {
        String path = request.getPath().value();
        for (var entry : customPathSettings.entrySet()) {
            if (path.matches(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public long getWindowMillis(ServerHttpRequest request) {
        Long result = getCustomPathSetting(request, config.getCustomWindows());
        if (result != null) {
            return result;
        }
        return config.getDefaultWindowMillis();
    }
}
