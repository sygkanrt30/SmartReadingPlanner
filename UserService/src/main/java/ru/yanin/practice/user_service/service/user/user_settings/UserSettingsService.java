package ru.yanin.practice.user_service.service.user.user_settings;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;

public interface UserSettingsService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);

    void changeStatusEmailNotification(Long userId, boolean isNeedEmailNotification);

    void changeStatusTgNotification(Long userId, boolean isNeedTgNotification);

    void changeStatusReadingReminder(Long userId, boolean isNeedReadingReminder);

    void changeStatusWeeklyReport(Long userId, boolean isNeedWeeklyReport);
}
