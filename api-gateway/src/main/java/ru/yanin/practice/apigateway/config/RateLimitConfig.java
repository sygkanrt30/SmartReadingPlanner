package ru.yanin.practice.apigateway.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gateway.rate-limiter")
@Data
@Validated
public class RateLimitConfig {

    /**
     * Включен ли rate limiter
     */
    private boolean enabled = true;

    /**
     * Стратегия при ошибках Redis
     * true - пропускать запросы (fail-open)
     * false - блокировать запросы (fail-close)
     */
    private boolean failOpen = true;

    /**
     * Лимитировать по endpoint
     */
    private boolean perEndpoint = true;

    /**
     * Лимитировать по HTTP методу
     */
    private boolean perMethod = false;

    /**
     * Лимитировать по пользователю
     */
    private boolean perUser = true;


    @Min(1)
    private int defaultMaxRequests = 100;

    @Min(1000)
    private long defaultWindowMillis = 60_000;

    /**
     * Кастомные лимиты для определенных путей
     * Ключ - regex pattern пути
     * Значение - максимальное количество запросов
     */
    private Map<String, Long> customLimits = new HashMap<>();

    /**
     * Кастомные окна для определенных путей
     * Ключ - regex pattern пути
     * Значение - окно в миллисекундах
     */
    private Map<String, Long> customWindows = new HashMap<>();

    /**
     * Пути, исключенные из rate limiting
     */
    private List<String> excludedPaths = List.of(
            "^/actuator/health$"
    );

    private String authPath = "/api/v1/users/auth/";

    public RateLimitConfig() {
    }
}
