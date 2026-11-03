package ru.yanin.practise.bookservice.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@Slf4j
public class OpenLibraryConfig {

    @Bean
    public RestClient openLibraryApi(
            @Value("${open-library.search.api.url}") String apiUrl,
            @Value("${open-library.search.api.user-agent}") String userAgent) {

        return RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", userAgent)
                .requestInterceptor((request, body, execution) -> {
                    log.debug("OpenLibrary request: {}", request.getURI());
                    return execution.execute(request, body);
                })
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        var factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(Duration.ofSeconds(8));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }
}
