package ru.yanin.practice.user_service.service.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.service.user.core.UserCoreService;
import ru.yanin.practice.user_service.service.user.reading_profile.ReadingProfileService;
import ru.yanin.practice.user_service.service.user.user_credentials.UserCredentialService;
import ru.yanin.practice.user_service.service.user.user_settings.UserSettingsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserSettingsService userSettingsService;
    private final ReadingProfileService readingProfileService;
    private final UserCoreService userCoreService;
    private final UserCredentialService userCredentialService;

    @Override
    @Transactional
    public void save(UserCoreForReg user) {
        Long savedUserId = userCoreService.save(user);
        log.debug("User core saved: {}", savedUserId);

        userCredentialService.save(user, savedUserId);
        log.trace("User credentials saved: {}", savedUserId);

        userSettingsService.save(user, savedUserId);
        log.trace("User settings saved: {}", savedUserId);

        readingProfileService.save(user, savedUserId);
        log.trace("Reading profile saved: {}", savedUserId);
    }

}
