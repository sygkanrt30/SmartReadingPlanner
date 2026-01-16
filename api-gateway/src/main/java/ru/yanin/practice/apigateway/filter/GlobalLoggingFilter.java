package ru.yanin.practice.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static ru.yanin.practice.apigateway.filter.HeaderName.*;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter, Ordered {


    private static final String START_TIME_ATTR_NAME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.trace("Incoming request: {} {} from {}",
                request.getMethod(),
                request.getURI(),
                request.getRemoteAddress());

        exchange.getAttributes().put(START_TIME_ATTR_NAME, System.currentTimeMillis());

        ServerHttpRequest modifiedRequest = request.mutate()
                .header(REQUEST_ID.value(), UUID.randomUUID().toString())
                .header(PROCESSED.value(), Boolean.TRUE.toString())
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doOnSuccess(_ -> logResponseTime(exchange))
                .doOnError(throwable ->
                        log.error("Error processing request: {}", throwable.getMessage()));

    }

    private void logResponseTime(ServerWebExchange exchange) {
        Long startTime = exchange.getAttribute("startTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            log.debug("{} Request processed in {} ms", exchange.getLogPrefix(), duration);
            exchange.getResponse().getHeaders()
                    .add(PROCESSING_TIME.value(), duration + "ms");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}