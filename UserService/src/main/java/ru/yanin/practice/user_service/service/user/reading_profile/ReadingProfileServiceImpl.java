package ru.yanin.practice.user_service.service.user.reading_profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yanin.practice.user_service.exception.EntityNotFoundException;
import ru.yanin.practice.user_service.exception.SaveEntityException;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;
import ru.yanin.practice.user_service.model.entity.Genre;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;
import ru.yanin.practice.user_service.model.mapper.ReadingProfileMapper;
import ru.yanin.practice.user_service.repository.ReadingProfileRepository;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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

    @Override
    @Transactional
    public void updateReadingGoals(Long userId, UpdateReadingGoalsRequest request) {
        log.info("Updating reading goals for user: {}", userId);
        ReadingProfile currentProfile = findReadingProfileOrThrow(userId);

        if (!request.hasChanges(currentProfile)) {
            log.debug("No changes detected in reading goals for user: {}", userId);
            return;
        }
        ReadingProfile updatedProfile = readingProfileMapper.updateGoalsFrom(request, currentProfile);
        readingProfileRepository.save(updatedProfile);
        log.debug("Reading goals updated successfully for user: {}", userId);
    }

    private ReadingProfile findReadingProfileOrThrow(Long userId) {
        return readingProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Reading profile not found for userId: %d", userId)
                ));
    }

    @Override
    public void addGenres(Set<String> genres, Long userId) {
        var readingProfile = findReadingProfileOrThrow(userId);

        Set<Genre> genresEnumSet = genres.stream()
                .filter(getPredicate())
                .map(Genre::valueOf)
                .collect(Collectors.toSet());

        readingProfile.preferredGenres().addAll(genresEnumSet);

        readingProfileRepository.save(readingProfile);
        log.debug("Reading profile preferred genres set expanded successfully for user: {}", userId);
    }

    @Override
    public void removeGenres(Set<String> genres, Long userId) {
        var readingProfile = findReadingProfileOrThrow(userId);

        Set<Genre> genresEnumSet = genres.stream()
                .filter(getPredicate())
                .map(Genre::valueOf)
                .collect(Collectors.toSet());

        readingProfile.preferredGenres().removeAll(genresEnumSet);

        readingProfileRepository.save(readingProfile);
        log.debug("Reading profile preferred genres set reduced successfully for user: {}", userId);
    }

    private Predicate<String> getPredicate() {
        return value -> {
            if (!Genre.isValidValue(value)) {
                log.trace("Invalid genre: {}", value);
                return false;
            }
            return true;
        };
    }
}
