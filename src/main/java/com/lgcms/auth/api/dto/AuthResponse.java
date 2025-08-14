package com.lgcms.auth.api.dto;

import com.lgcms.auth.remote.member.dto.MemberResponse.MemberInfoResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {
    public record SignInResponse(
        Boolean alreadyMember,
        TokenResponse tokens,
        MemberInfoResponse memberInfo
    ) {
        public static SignInResponse memberDto(TokenResponse tokens, MemberInfoResponse memberInfo) {
            return new SignInResponse(true, tokens, memberInfo);
        }

        public static SignInResponse notMemberDto(TokenResponse tokens, MemberInfoResponse memberInfo) {
            return new SignInResponse(false, tokens, memberInfo);
        }
    }

    public record TokenResponse(
        String accessToken,
        String refreshToken
    ) {
        public static TokenResponse toDto(String accessToken, String refreshToken) {
            return new TokenResponse(accessToken, refreshToken);
        }
    }

    public record LogoutResponse(
        Boolean isDone
    ) {
        public static LogoutResponse toDto(Boolean isDone) {
            return new LogoutResponse(isDone);
        }
    }

    public record SignoutResponse(
        Boolean isDone
    ) {
        public static SignoutResponse toDto(Boolean isDone) {
            return new SignoutResponse(isDone);
        }
    }
}
