package com.lgcms.auth.api.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRequest {
    public record SignInRequest(
        String idTokenString
    ) {
    }
}
