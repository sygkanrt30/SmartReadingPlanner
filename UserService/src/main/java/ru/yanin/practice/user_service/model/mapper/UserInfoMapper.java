package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserInfoMapper {

    @Mapping(target = "role", constant = "USER")
    UserInfoForTokenDto toUserInfoDto(String username, Long savedUserId);
}
