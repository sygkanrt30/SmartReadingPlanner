package ru.yanin.practice.user_service.service.user.reading_profile;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;

public interface ReadingProfileService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);
}
