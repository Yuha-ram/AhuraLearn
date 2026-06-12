package com.ahuralearn.auth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Login Form Entity")
public class LoginFormDTO {

    @NotBlank(message = "username can't be empty")
    @Schema(description = "username", example = "yuha")
    @Pattern(
            regexp = "^[a-zA-Z0-9]{4,}$",
            message = "Username must be at least 4 characters long and contain only letters or numbers"
    )
    private String username;

    @NotBlank(message = "pwd can't be empty")
    @Schema(description = "password", example = "yuha0412")
    @Size(min = 6, message = "Pwd must be at least 6 characters long")
    private String password;
}
