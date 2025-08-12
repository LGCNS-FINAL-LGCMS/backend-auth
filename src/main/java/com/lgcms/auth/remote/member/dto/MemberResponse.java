package com.lgcms.auth.remote.member.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {
    public record SignupResponse(
        Boolean alreadyExist,
        String memberId,
        String role
    ) {
    }
}
