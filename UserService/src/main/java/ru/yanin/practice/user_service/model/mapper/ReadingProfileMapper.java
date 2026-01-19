package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.request.user.reading_profle.UpdateReadingGoalsRequest;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadingProfileMapper {

    ReadingProfile toReadingProfile(UserCoreForReg userCoreForReg, Long userId);

    @Mapping(target = "id", source = "currentReadingProfile.id")
    @Mapping(target = "userId", source = "currentReadingProfile.userId")
    @Mapping(target = "createdAt", source = "currentReadingProfile.createdAt")
    @Mapping(target = "preferredGenres", source = "currentReadingProfile.preferredGenres")
    @Mapping(target = "timezone", source = "currentReadingProfile.timezone")
    @Mapping(target = "wordsPerMin",
            expression = "java(updateGoal(request.wordsPerMin(), currentReadingProfile.wordsPerMin()))")
    @Mapping(target = "minPerDay",
            expression = "java(updateGoal(request.minPerDay(), currentReadingProfile.minPerDay()))")
    @Mapping(target = "bookPerMonth",
            expression = "java(updateGoal(request.bookPerMonth(), currentReadingProfile.bookPerMonth()))")
    ReadingProfile updateGoalsFrom(UpdateReadingGoalsRequest request, ReadingProfile currentReadingProfile);

    default int updateGoal(Integer goal, int currentGoal) {
        return goal != null ? goal : currentGoal;
    }
}
