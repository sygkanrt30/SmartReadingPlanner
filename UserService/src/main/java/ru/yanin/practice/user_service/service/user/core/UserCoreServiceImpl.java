package ru.yanin.practice.user_service.service.user.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.exception.UpdateEntityException;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.core.FirstAndLastNameDto;
import ru.yanin.practice.user_service.model.entity.UserCore;
import ru.yanin.practice.user_service.model.mapper.UserCoreMapper;
import ru.yanin.practice.user_service.repository.UserCoreRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCoreServiceImpl implements UserCoreService {

    private final UserCoreMapper userCoreMapper;
    private final UserCoreRepository userCoreRepository;

    @Override
    @Transactional
    public Long save(UserCoreForReg userCoreForReg) {
        var userCore = userCoreMapper.toUserCore(userCoreForReg);
        try {
            UserCore savedEntity = userCoreRepository.save(userCore);
            return savedEntity.id();
        } catch (Exception e) {
            throw new SaveEntityException(e.getMessage(), e);
        }
    }

    @Override
    public void changeFullName(FirstAndLastNameDto dto, Long userId) {
        try {
            userCoreRepository.updateFirstAndLastNameByUserId(userId, dto.firstName(), dto.lastName());
        } catch (Exception e) {
            throw new UpdateEntityException(e.getMessage(), e);
        }
    }
}
