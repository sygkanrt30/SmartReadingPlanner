package ru.yanin.practice.user_service.service.highlevel.registration;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.response.RegResponse;

public interface RegistrationService {

    RegResponse register(UserCoreForReg userCoreForReg);
}
