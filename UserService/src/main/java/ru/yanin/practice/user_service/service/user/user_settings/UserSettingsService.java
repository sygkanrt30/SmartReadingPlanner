package ru.yanin.practice.user_service.service.user.user_settings;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;

public interface UserSettingsService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);
}
