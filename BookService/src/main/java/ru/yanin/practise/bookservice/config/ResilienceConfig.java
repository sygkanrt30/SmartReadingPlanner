package ru.yanin.practise.bookservice.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

@Configuration
@Slf4j
public class ResilienceConfig {

    @Value("${google.books.retry-name}")
    private String googleRetryName;

    @Value("${open-library.search.retry-name}")
    private String openLibraryRetryName;

    @Value("${user-service.api.retry-name}")
    private String userServiceRetryName;

    @Bean
    public RetryRegistry retryRegistry() {
        var googleConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnException(this::logException)
                .build();

        var openLibraryConfig = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(500))
                .retryOnException(this::logException)
                .build();

        var userServiceConfig = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(1000))
                .retryOnException(this::logException)
                .build();

        var registry = RetryRegistry.of(googleConfig);
        registry.addConfiguration(openLibraryRetryName, openLibraryConfig);
        registry.addConfiguration(userServiceRetryName, userServiceConfig);
        return registry;
    }

    private boolean logException(Throwable e) {
        boolean shouldRetry = e instanceof ResourceAccessException ||
                e instanceof HttpStatusCodeException;
        if (shouldRetry) {
            log.warn("Will retry after error: {}", e.getMessage());
        }
        return shouldRetry;
    }

    @Bean
    public Retry googleBooksRetry(RetryRegistry registry) {
        return registry.retry(googleRetryName);
    }

    @Bean
    public Retry openLibraryRetry(RetryRegistry registry) {
        return registry.retry(openLibraryRetryName);
    }

    @Bean
    public Retry userServiceRetry(RetryRegistry registry) {
        return registry.retry(userServiceRetryName);
    }

    @Bean
    public TimeLimiter timeLimiter() {
        var config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .build();
        return TimeLimiter.of(config);
    }
}
