package ru.yanin.practise.bookservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GoogleBooksConfig {

    @Bean
    public RestClient googleBooksRestClient(
            @Value("${google.books.api.url}") String apiUrl) {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }
}
