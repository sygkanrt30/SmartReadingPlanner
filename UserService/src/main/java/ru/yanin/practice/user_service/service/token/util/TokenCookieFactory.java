package ru.yanin.practice.user_service.service.token.util;

import lombok.Setter;
import ru.yanin.practice.user_service.model.Token;
import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

@Setter
public class TokenCookieFactory implements Function<UserInfoForTokenDto, Token> {

    private Duration tokenTtl = Duration.ofDays(1);

    @Override
    public Token apply(UserInfoForTokenDto userInfo) {
        var now = Instant.now();
        return new Token(
                UUID.randomUUID(),
                userInfo.username(),
                userInfo.role(),
                now,
                now.plus(tokenTtl)
        );
    }
}
