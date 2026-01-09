package ru.yanin.practice.user_service.service.token;

import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;

public interface TokenService {

    TokenServiceImpl.TokenTransferDto createStringToken(UserInfoForTokenDto userInfo);
}
