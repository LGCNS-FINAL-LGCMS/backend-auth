package com.lgcms.auth.api.open;

import com.lgcms.auth.api.dto.AuthRequest.RefreshTokenRequest;
import com.lgcms.auth.api.dto.AuthRequest.SignInRequest;
import com.lgcms.auth.api.dto.AuthResponse.SignInResponse;
import com.lgcms.auth.api.dto.AuthResponse.TokenResponse;
import com.lgcms.auth.remote.member.dto.SocialType;
import com.lgcms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/auth/")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * true를 반환하면 이미 가입된 사용자, false를 반환하면 신규 가입한 사용자
     *
     * @param request
     * @return
     */
    @PostMapping("/sign-in/google")
    public ResponseEntity<SignInResponse> signInWithGoogle(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request.idTokenString(), SocialType.GOOGLE));
    }

    @PostMapping("/refresh/token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Boolean> logout(@RequestHeader("X-JTI") String jti) {
        return ResponseEntity.ok(authService.logout(jti));
    }

    @PostMapping("/sign-out/google")
    public ResponseEntity<Boolean> signout(
        @RequestHeader("X-JTI") String jti,
        @RequestHeader("X-USER-ID") Long memberId
    ) {
        return ResponseEntity.ok(authService.signout(jti, memberId));
    }
}
