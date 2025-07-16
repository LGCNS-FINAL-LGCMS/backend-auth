package com.lgcms.auth.remote.member.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {
    public record SignupRequest(
        String sub,
        String email,
        SocialType socialType
    ) {
        public static SignupRequest toDto(String sub, String email, SocialType socialType) {
            return new SignupRequest(sub, email, socialType);
        }
    }
}
