package ru.yanin.practise.bookservice.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Value("${google.books.retry-name}")
    private String retryName;

    @Bean
    public RetryRegistry retryRegistry() {
        var config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnException(e ->
                        e instanceof ResourceAccessException || e instanceof HttpStatusCodeException)
                .build();
        return RetryRegistry.of(config);
    }

    @Bean
    public Retry retryTemplate(RetryRegistry registry) {
        return registry.retry(retryName);
    }
}
