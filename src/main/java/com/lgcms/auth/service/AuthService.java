package com.lgcms.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.lgcms.auth.api.dto.AuthResponse.SignInResponse;
import com.lgcms.auth.api.dto.AuthResponse.TokenResponse;
import com.lgcms.auth.common.dto.exception.BaseException;
import com.lgcms.auth.common.jwt.JwtTokenProvider;
import com.lgcms.auth.common.jwt.JwtType;
import com.lgcms.auth.remote.member.RemoteMemberService;
import com.lgcms.auth.remote.member.dto.MemberRequest.SignupRequest;
import com.lgcms.auth.remote.member.dto.MemberResponse.SignupResponse;
import com.lgcms.auth.remote.member.dto.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lgcms.auth.common.dto.exception.AuthError.MEMBER_SERVER_ERROR;
import static com.lgcms.auth.common.dto.exception.AuthError.NO_SUCH_SOCIAL_TYPE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RemoteMemberService remoteMemberService;
    private final OauthUtil oauthUtil;
    private final JwtTokenProvider jwtTokenProvider;

    public SignInResponse signIn(String idTokenString, SocialType socialType) {
        String sub = null;
        String email = null;
        switch (socialType) {
            case GOOGLE -> {
                GoogleIdToken.Payload payload = oauthUtil.getSubFromGoogleIdToken(idTokenString);
                sub = payload.getSubject();
                email = payload.getEmail();
            }
            case null, default -> {
                throw new BaseException(NO_SUCH_SOCIAL_TYPE);
            }
        }
        if (sub == null) {
            throw new BaseException(NO_SUCH_SOCIAL_TYPE);
        }

        SignupResponse response = remoteMemberService.signup(SignupRequest.toDto(sub, email, socialType)).getBody();
        if (response == null) {
            throw new BaseException(MEMBER_SERVER_ERROR);
        }
        if (response.alreadyExist()) {
            return SignInResponse.memberDto(createTokens(response.memberId()));
        }
        return SignInResponse.notMemberDto(createTokens(response.memberId()));
    }

    public TokenResponse refreshToken(String refreshToken) {
        // 기존의 refreshToken 블랙리스트 등록 할까 말까
        String memberId = jwtTokenProvider.getMemberId(refreshToken, JwtType.REFRESH_TOKEN);
        return createTokens(memberId);
    }

    private TokenResponse createTokens(String memberId) {
        long currentTimeMillis = System.currentTimeMillis();
        String accessToken = jwtTokenProvider.createJwt(memberId, JwtType.ACCESS_TOKEN, currentTimeMillis);
        String refreshToken = jwtTokenProvider.createJwt(memberId, JwtType.REFRESH_TOKEN, currentTimeMillis);
        return TokenResponse.toDto(accessToken, refreshToken);
    }
}
