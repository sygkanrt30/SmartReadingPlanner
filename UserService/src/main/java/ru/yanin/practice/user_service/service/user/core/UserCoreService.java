package ru.yanin.practice.user_service.service.user.core;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.core.FirstAndLastNameDto;

public interface UserCoreService {

    Long save(UserCoreForReg userCoreForReg);

    void changeFullName(FirstAndLastNameDto dto, Long userId);
}
