package ru.yanin.practice.user_service.service.user.user_settings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.EntityNotFoundException;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.entity.UserSetting;
import ru.yanin.practice.user_service.model.mapper.UserSettingsMapper;
import ru.yanin.practice.user_service.repository.UserSettingsRepository;
import ru.yanin.shared.language.Language;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserSettingsMapper userSettingsMapper;

    @Transactional
    @Override
    public void save(UserCoreForReg userCoreForReg, Long savedUserId) {
        var userSettings = userSettingsMapper.toUserSetting(userCoreForReg, savedUserId);
        try {
            userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            throw new SaveEntityException(e.getMessage(), e);
        }
    }

    @Override
    public void changeStatusEmailNotification(Long userId, boolean isNeedEmailNotification) {
        userSettingsRepository.updateStatusEmailNotification(userId, isNeedEmailNotification);
        log.debug("Change status email notification to {}", isNeedEmailNotification);
    }

    @Override
    public void changeStatusTgNotification(Long userId, boolean isNeedTgNotification) {
        userSettingsRepository.updateStatusTgNotification(userId, isNeedTgNotification);
        log.debug("Change status tg notification to {}", isNeedTgNotification);
    }

    @Override
    public void changeStatusReadingReminder(Long userId, boolean isNeedReadingReminder) {
        userSettingsRepository.updateStatusReadingReminder(userId, isNeedReadingReminder);
        log.debug("Change status reading reminder to {}", isNeedReadingReminder);
    }

    @Override
    public void changeStatusWeeklyReport(Long userId, boolean isNeedWeeklyReport) {
        userSettingsRepository.updateStatusWeeklyReport(userId, isNeedWeeklyReport);
        log.debug("Change status weekly report to {}", isNeedWeeklyReport);
    }

    @Override
    public Language getLangByUserId(Long userId) {
        UserSetting userSetting = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User settings not found by userId: " + userId));
        return userSetting.lang();
    }
}
