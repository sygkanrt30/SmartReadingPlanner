package ru.yanin.practice.user_service.service.user.reading_profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.mapper.ReadingProfileMapper;
import ru.yanin.practice.user_service.repository.ReadingProfileRepository;

@Service
@RequiredArgsConstructor
public class ReadingProfileServiceImpl implements ReadingProfileService {

    private final ReadingProfileRepository readingProfileRepository;
    private final ReadingProfileMapper readingProfileMapper;

    @Override
    @Transactional
    public void save(UserCoreForReg userCoreForReg, Long savedUserId) {
        var readingProfile = readingProfileMapper.toReadingProfile(userCoreForReg, savedUserId);
        try {
            readingProfileRepository.save(readingProfile);
        } catch (Exception e) {
            throw new SaveEntityException(e.getMessage(), e);
        }
    }
}
