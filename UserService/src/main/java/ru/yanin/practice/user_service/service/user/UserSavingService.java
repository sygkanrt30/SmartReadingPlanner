package ru.yanin.practice.user_service.service.user;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;

public interface UserSavingService {

    void save(UserCoreForReg user);
}
