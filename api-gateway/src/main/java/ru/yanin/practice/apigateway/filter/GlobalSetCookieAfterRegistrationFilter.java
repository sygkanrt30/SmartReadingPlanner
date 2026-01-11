package ru.yanin.practice.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import ru.yanin.practice.apigateway.cookie.MyServerHttpResponseDecorator;

@Component
@Slf4j
public class GlobalSetCookieAfterRegistrationFilter
        extends AbstractGatewayFilterFactory<GlobalSetCookieAfterRegistrationFilter.Config> {

    public GlobalSetCookieAfterRegistrationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpResponse originalResponse = exchange.getResponse();
            var decoratedResponse = new MyServerHttpResponseDecorator(originalResponse);
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
        };
    }

    public static class Config {
    }
}