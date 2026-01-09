package ru.yanin.practice.user_service.service.user.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.entity.UserCore;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.mapper.UserCoreMapper;
import ru.yanin.practice.user_service.repository.UserCoreRepository;

@Service
@RequiredArgsConstructor
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
}
