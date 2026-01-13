package ru.yanin.practice.user_service.service.user.user_credentials;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;

import java.util.UUID;

public interface UserCredentialService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);
}
