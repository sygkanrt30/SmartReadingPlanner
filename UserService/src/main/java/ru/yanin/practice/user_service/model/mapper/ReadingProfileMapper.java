package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.entity.ReadingProfile;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReadingProfileMapper {

    ReadingProfile toReadingProfile(UserCoreForReg userCoreForReg, Long userId);
}
