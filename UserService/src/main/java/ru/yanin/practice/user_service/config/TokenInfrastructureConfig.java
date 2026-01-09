package ru.yanin.practice.user_service.config;

import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yanin.practice.user_service.service.token.TokenService;
import ru.yanin.practice.user_service.service.token.TokenServiceImpl;
import ru.yanin.practice.user_service.service.token.util.TokenCookieJweStringSerializer;

@Configuration
public class TokenInfrastructureConfig {

    @Bean
    public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey
    ) throws Exception {
        return new TokenCookieJweStringSerializer(new DirectEncrypter(
                OctetSequenceKey.parse(cookieTokenKey)
        ));
    }

    @Bean
    public TokenService tokenService(
            TokenCookieJweStringSerializer tokenCookieJweStringSerializer) {

        var tokenService = new TokenServiceImpl();
        tokenService.setTokenStringSerializer(tokenCookieJweStringSerializer);
        return tokenService;
    }
}
