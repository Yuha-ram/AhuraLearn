package com.ahuralearn.auth.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.ahuralearn.auth.domain.dto.LoginFormDTO;
import com.ahuralearn.auth.domain.dto.SignupFormDTO;
import com.ahuralearn.auth.domain.vo.LoginVO;
import com.ahuralearn.auth.service.IAccountService;
import com.ahuralearn.common.config.cloud.AliConfig;
import com.ahuralearn.common.config.cloud.AliProperties;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.JWTUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.user.domain.po.User;
import com.ahuralearn.user.enums.UserStatus;
import com.ahuralearn.user.service.IUserService;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final IUserService userService;
    private final AliProperties aliProperties;

    @Override
    public LoginVO login(LoginFormDTO loginFormDTO) {
        // 1.parameter validation
        String username = loginFormDTO.getUsername();
        String password = loginFormDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 2.get user object
        User user = userService.queryByUsernameAndPwd(username, password);
        if (user == null)
            throw new BusinessException("Username or password incorrect");

        // 3.verify whether the user status is ok
        if (user.getStatus() == UserStatus.DISABLED)
            throw new BusinessException("User has been disabled");

        // 4.generate accessToken & refreshToken
        return generate2jwt(user.getId(), user.getRole().getDesc());
    }

    @Override
    public void logout() {
        Long userId = UserContext.getUser();
        log.info("user: {} performed logout operation", userId);
    }

    @Override
    public void register(SignupFormDTO signupFormDTO) {
        String username = signupFormDTO.getUsername();
        String email = signupFormDTO.getEmail();
        String password = signupFormDTO.getPassword();
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(email))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 1.validate whether the username is duplicated
        checkUsernameIsAvailable(username);

        // 2.validate whether the email is duplicated
        checkEmailIsAvailable(email);

        // 3.username and email both ok, save the data to db
        User user = new User();

        // 4.encrypt password
        String pwd = BCrypt.hashpw(password);
        user.setUsername(username).setEmail(email).setPassword(pwd).setAvatar(aliProperties.getDeafultAvatar());
        userService.save(user);
    }

    private void checkEmailIsAvailable(String email) {
        userService.checkEmailIsAvailable(email);
    }

    public void checkUsernameIsAvailable(String username) {
        userService.checkUsernameIsAvailable(username);
    }

    @Override
    public LoginVO refreshToken(String token) {
        if (StringUtils.isEmpty(token))
            throw new BusinessException(ResultCode.UNAUTHORIZED);

        // 1.parse token
        DecodedJWT decodedJWT = JWTUtils.verifyToken(token);

        // 2.refreshToken validation
        if (!"REFRESH".equals(decodedJWT.getClaim("type").asString())) {
            // the jwt isn't refreshToken
            throw new BusinessException(ResultCode.TOKEN_TYPE_ERROR);
        }

        // 3.user status validation. if disabled, not allowed
        Long userId = decodedJWT.getClaim("userId").asLong();
        User user = userService.checkStatus(userId);

        // 4.refresh token
        return generate2jwt(userId, user.getRole().getDesc());
    }

    private LoginVO generate2jwt(Long userId, String role) {
        Instant now = Instant.now();

        String accessToken = JWTUtils.createToken(
                Map.of("userId", userId, "role", role),
                now.plus(30, ChronoUnit.MINUTES));

        String refreshToken = JWTUtils.createToken(
                Map.of("userId", userId, "type", "REFRESH"),
                now.plus(7, ChronoUnit.DAYS));

        return new LoginVO(accessToken, refreshToken);
    }
}
