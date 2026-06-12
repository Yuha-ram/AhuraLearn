package com.ahuralearn.auth.controller;

import com.ahuralearn.auth.domain.dto.LoginFormDTO;
import com.ahuralearn.auth.domain.dto.SignupFormDTO;
import com.ahuralearn.auth.domain.vo.LoginVO;
import com.ahuralearn.auth.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "accountController")
public class AccountController {

    private final IAccountService accountService;

    @Operation(summary = "User Login", description = "login and generate jwt")
    @PostMapping("/login")
    public LoginVO login(@RequestBody @Validated LoginFormDTO loginFormDTO) {
        return accountService.login(loginFormDTO);
    }

    @Operation(summary = "User Logout")
    @PostMapping("/logout")
    public void logout() {
        accountService.logout();
    }

    @Operation(summary = "User Signup")
    @PostMapping("/register")
    public void register(@RequestBody @Validated SignupFormDTO signupFormDTO) {
        accountService.register(signupFormDTO);
    }

    @Operation(summary = "Check username availability")
    @GetMapping("/users/exists")
    public void checkUsernameExists(@RequestParam(value = "username") String name) {
        accountService.checkUsernameIsAvailable(name);
    }

    @Operation(summary = "Refresh token", description = "refresh to get a new accessToken and new refreshToken")
    @PostMapping("/refresh")
    public LoginVO refreshToken(@RequestHeader("Authorization-Refresh") String token) {
        return accountService.refreshToken(token);
    }
}
