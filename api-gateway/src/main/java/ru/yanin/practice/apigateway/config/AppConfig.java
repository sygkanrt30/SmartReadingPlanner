package ru.yanin.practice.apigateway.config;

import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yanin.practice.apigateway.token.TokenCookieJweStringDeserializer;

@Configuration
public class AppConfig {

    @Bean
    public TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey) throws Exception {
        return new TokenCookieJweStringDeserializer(
                new DirectDecrypter(
                        OctetSequenceKey.parse(cookieTokenKey)
                )
        );
    }
}
