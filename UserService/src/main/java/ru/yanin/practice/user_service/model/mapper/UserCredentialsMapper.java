package ru.yanin.practice.user_service.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.yanin.practice.user_service.model.entity.UserCredentials;
import ru.yanin.practice.user_service.model.dto.request.UserCredentialsForReg;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserCredentialsMapper {

    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "password", expression = "java(encryptPassword(userCredentialsForReg.password()))")
    UserCredentials toUserCredentials(UserCredentialsForReg userCredentialsForReg, Long userId);


    default String encryptPassword(String password) {
        var encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
