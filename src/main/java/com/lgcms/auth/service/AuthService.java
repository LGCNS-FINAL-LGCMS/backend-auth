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
import com.lgcms.auth.repository.JtiRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.lgcms.auth.common.dto.exception.AuthError.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final RemoteMemberService remoteMemberService;
    private final OauthUtil oauthUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final JtiRedisRepository jtiRedisRepository;

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
        String prevJti = jwtTokenProvider.getJti(refreshToken, JwtType.REFRESH_TOKEN);
        if (jtiRedisRepository.isBlackList(prevJti))
            throw new BaseException(USED_REFRESH_TOKEN);
        String memberId = jwtTokenProvider.getMemberId(refreshToken, JwtType.REFRESH_TOKEN);
        TokenResponse tokenResponse = createTokens(memberId);
        jtiRedisRepository.addJti(prevJti);
        return tokenResponse;
    }

    private TokenResponse createTokens(String memberId) {
        long currentTimeMillis = System.currentTimeMillis();
        String jti = jwtTokenProvider.createJti();
        String accessToken = jwtTokenProvider.createJwt(memberId, JwtType.ACCESS_TOKEN, currentTimeMillis, jti);
        String refreshToken = jwtTokenProvider.createJwt(memberId, JwtType.REFRESH_TOKEN, currentTimeMillis, jti);
        return TokenResponse.toDto(accessToken, refreshToken);
    }

    public boolean logout(String jti) {
        return jtiRedisRepository.addJti(jti);
    }

    public boolean signout(String jti, Long memberId) {
        remoteMemberService.signout(memberId);
        jtiRedisRepository.addJti(jti);
        return true;
    }
}
