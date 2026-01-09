package ru.yanin.practice.user_service.service.user.user_settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.mapper.UserSettingsMapper;
import ru.yanin.practice.user_service.repository.UserSettingsRepository;

@Service
@RequiredArgsConstructor
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
}
