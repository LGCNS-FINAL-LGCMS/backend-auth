package com.lgcms.auth.remote.member.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {
    public record SignupResponse(
        Boolean alreadyExist,
        MemberInfoResponse memberInfo
    ) {
    }

    public record MemberInfoResponse(
        Long memberId,
        String nickname,
        String role,
        Boolean desireLecturer,
        List<Category> categories
    ) {
    }

    public record Category(
        Long id,
        String name
    ) {
    }
}
