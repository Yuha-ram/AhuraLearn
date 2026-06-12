package com.ahuralearn.auth.service;

import com.ahuralearn.auth.domain.dto.LoginFormDTO;
import com.ahuralearn.auth.domain.dto.SignupFormDTO;
import com.ahuralearn.auth.domain.vo.LoginVO;

public interface IAccountService {
    LoginVO login(LoginFormDTO loginFormDTO);

    void logout();

    void register(SignupFormDTO signupFormDTO);

    void checkUsernameIsAvailable(String name);

    LoginVO refreshToken(String token);
}
