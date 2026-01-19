package ru.yanin.practice.user_service.service.user.reading_profile;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;

public interface ReadingProfileService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);

    void updateReadingGoals(Long userId, UpdateReadingGoalsRequest request);
}
