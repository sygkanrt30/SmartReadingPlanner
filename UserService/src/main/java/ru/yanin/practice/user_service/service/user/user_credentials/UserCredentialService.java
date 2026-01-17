package ru.yanin.practice.user_service.service.user.user_credentials;

import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;

public interface UserCredentialService {

    void save(UserCoreForReg userCoreForReg, Long savedUserId);

    void changeEmailVerificationStatus(String email);

    boolean checkUserIdAndEmailBelongToSameUser(String email, Long userId);
}
