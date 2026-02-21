package ru.yanin.practise.bookservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UserServiceInternalRequestConfig {

    @Bean("userServiceApi")
    public RestClient internalUserServiceApi(@Value("${user-service.api.internal.base-url}") String baseUrl) {
        return RestClient.create(baseUrl);
    }
}
