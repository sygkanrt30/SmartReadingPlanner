package ru.yanin.practise.bookservice.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Value("${google.books.retry-name}")
    private String googleRetryName;

    @Value("${open-library.search.retry-name}")
    private String openLibraryRetryName;

    @Bean
    public RetryRegistry retryRegistry() {
        var googleConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnException(e ->
                        e instanceof ResourceAccessException || e instanceof HttpStatusCodeException)
                .build();

        var openLibraryConfig = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(500))
                .retryOnException(e ->
                        e instanceof ResourceAccessException || e instanceof HttpStatusCodeException)
                .build();

        var registry = RetryRegistry.of(googleConfig);
        registry.addConfiguration(openLibraryRetryName, openLibraryConfig);
        return registry;
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
    public TimeLimiter timeLimiter() {
        var config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(10))
                .build();
        return TimeLimiter.of(config);
    }
}
