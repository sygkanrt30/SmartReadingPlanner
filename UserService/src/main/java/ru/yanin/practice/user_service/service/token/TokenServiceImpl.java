package ru.yanin.practice.user_service.service.token;

import lombok.Setter;
import ru.yanin.practice.token.Token;
import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;
import ru.yanin.practice.user_service.service.token.util.TokenCookieFactory;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;


@Setter
public class TokenServiceImpl implements TokenService {

    private Function<UserInfoForTokenDto, Token> tokenCookieFactory = new TokenCookieFactory();
    private Function<Token, String> tokenStringSerializer = Objects::toString;

    @Override
    public TokenTransferDto createStringToken(UserInfoForTokenDto userInfo) {
        var token = tokenCookieFactory.apply(userInfo);
        String stringToken = tokenStringSerializer.apply(token);
        return new TokenTransferDto(token.id(),stringToken);
    }

    public record TokenTransferDto(UUID tokenId, String token) {}
}
