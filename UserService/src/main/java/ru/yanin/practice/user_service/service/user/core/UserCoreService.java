package ru.yanin.practice.user_service.service.user.core;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;

public interface UserCoreService {

    Long save(UserCoreForReg userCoreForReg);
}
