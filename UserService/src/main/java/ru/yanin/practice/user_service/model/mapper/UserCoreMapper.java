package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.entity.UserCore;
import ru.yanin.practice.user_service.model.dto.request.auth.UserCoreForReg;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCoreMapper {

    @Mapping(target = "firstName", expression = "java(userCoreForReg.firstName().toLowerCase())")
    @Mapping(target = "lastName", expression = "java(userCoreForReg.lastName().toLowerCase())")
    UserCore toUserCore(UserCoreForReg userCoreForReg);
}
