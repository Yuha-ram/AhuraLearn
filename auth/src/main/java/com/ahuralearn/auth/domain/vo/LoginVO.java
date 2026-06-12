package com.ahuralearn.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "JWT Entity")
public class LoginVO {
    private String accessToken;
    private String refreshToken;
}
