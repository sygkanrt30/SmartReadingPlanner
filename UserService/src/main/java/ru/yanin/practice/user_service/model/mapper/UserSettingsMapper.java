package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.entity.UserSetting;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSettingsMapper {

    @Mapping(target = "lang", constant = "en")
    UserSetting toUserSetting(UserCoreForReg userCoreForReg, Long userId);
}
