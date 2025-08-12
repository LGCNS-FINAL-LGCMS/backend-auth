package com.lgcms.auth.common.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@DisplayName("JwtTokenProvider로 ")
class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("생성한 jwt의 jti를 추출할 수 있다")
    void getJti() {
        // given
        String memberId = "1";
        long currentTimeMillis = System.currentTimeMillis();
        String jti = jwtTokenProvider.createJti();
        String accessToken = jwtTokenProvider.createJwt(memberId, JwtType.ACCESS_TOKEN, currentTimeMillis, jti, "STUDENT");
        String refreshToken = jwtTokenProvider.createJwt(memberId, JwtType.REFRESH_TOKEN, currentTimeMillis, jti, "STUDENT");

        // when
        String accessJti = jwtTokenProvider.getJti(accessToken, JwtType.ACCESS_TOKEN);
        String refreshJti = jwtTokenProvider.getJti(refreshToken, JwtType.REFRESH_TOKEN);

        // then
        assertThat(accessJti).isEqualTo(refreshJti);
    }
}