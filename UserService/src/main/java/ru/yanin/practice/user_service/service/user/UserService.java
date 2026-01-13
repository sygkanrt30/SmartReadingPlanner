package ru.yanin.practice.user_service.service.user;

import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;

public interface UserService {

    void save(UserCoreForReg user);

}
