package ru.yanin.practice.user_service.service.user.user_credentials;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.entity.UserCredentials;
import ru.yanin.practice.user_service.model.mapper.UserCredentialsMapper;
import ru.yanin.practice.user_service.repository.UserCredentialsRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCredentialServiceImpl implements UserCredentialService {

    private final UserCredentialsMapper userCredentialsMapper;
    private final UserCredentialsRepository userCredentialsRepository;

    @Override
    @Transactional
    public void save(UserCoreForReg userCoreForReg, Long savedUserId) {
        var userCredentials = userCredentialsMapper.toUserCredentials(userCoreForReg.userCredentials(), savedUserId);
        try {
            userCredentialsRepository.save(userCredentials);
        } catch (Exception e) {
            throw new SaveEntityException(e.getMessage(), e);
        }
    }

    @Override
    public void changeEmailVerificationStatus(String email) {
        userCredentialsRepository.updateEmailVerificationStatus(email);
        log.debug("Email verification status in DB changed");
    }

    @Override
    public boolean checkUserIdAndEmailBelongToSameUser(String email, Long userId) {
        UserCredentials userCredentials = userCredentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User credentials not found by userId: " + userId));
        boolean isBelongToSameUser = userCredentials.email().equals(email);
        log.trace("User id and email belong to the same user: {}", isBelongToSameUser);
        return isBelongToSameUser;
    }
}
