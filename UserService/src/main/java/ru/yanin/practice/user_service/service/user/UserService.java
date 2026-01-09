package ru.yanin.practice.user_service.service.user;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;

import java.util.UUID;

public interface UserService {

    UserInfoForTokenDto save(UserCoreForReg user);

    void addTokenIdToUser(UUID tokenId, Long userId);
}
