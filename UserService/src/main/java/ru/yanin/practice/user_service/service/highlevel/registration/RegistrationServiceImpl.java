package ru.yanin.practice.user_service.service.highlevel.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yanin.practice.user_service.model.dto.request.UserCoreForReg;
import ru.yanin.practice.user_service.model.dto.response.RegResponse;
import ru.yanin.practice.user_service.model.dto.response.UserInfoForTokenDto;
import ru.yanin.practice.user_service.service.token.TokenService;
import ru.yanin.practice.user_service.service.token.TokenServiceImpl;
import ru.yanin.practice.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public RegResponse register(UserCoreForReg userCoreForReg) {
        UserInfoForTokenDto userInfo = userService.save(userCoreForReg);
        TokenServiceImpl.TokenTransferDto tokenDto = tokenService.createStringToken(userInfo);
        userService.addTokenIdToUser(tokenDto.tokenId(), userInfo.id());
        log.info("Sign up successful; user id: {}", userInfo.id());
        return new RegResponse(
                "Sign up successful",
                tokenDto.token(),
                userInfo.username(),
                userInfo.id()
        );
    }
}
