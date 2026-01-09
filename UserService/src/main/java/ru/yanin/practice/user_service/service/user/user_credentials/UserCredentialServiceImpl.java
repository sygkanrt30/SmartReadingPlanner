package ru.yanin.practice.user_service.service.user.user_credentials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.mapper.UserCredentialsMapper;
import ru.yanin.practice.user_service.repository.UserCredentialsRepository;

import java.util.UUID;

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
    public void addTokenIdToUser(UUID tokenId, Long userId) {
        userCredentialsRepository.addTokenIdToUser(tokenId, userId);
    }
}
