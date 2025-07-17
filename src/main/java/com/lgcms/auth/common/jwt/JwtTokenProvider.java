package com.lgcms.auth.common.jwt;

import com.lgcms.auth.common.dto.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.lgcms.auth.common.dto.exception.AuthError.*;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private SecretKey refreshSecretKey;
    private SecretKey accessSecretKey;
    private @Value("${jwt.issuer}") String issuer;
    private @Value("${jwt.refresh_jwt_secret}") String refreshJwtSecret;
    private @Value("${jwt.access_jwt_secret}") String accessJwtSecret;
    private @Value("${jwt.refresh_expired_time}") Long refreshExpiredTime;
    private @Value("${jwt.access_expired_time}") Long accessExpiredTime;

    @PostConstruct
    protected void init() {
        refreshSecretKey = new SecretKeySpec(refreshJwtSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
        accessSecretKey = new SecretKeySpec(accessJwtSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS512.key().build().getAlgorithm());
    }

    public String createJwt(String memberId, JwtType tokenType, Long currentTimeMillis) {
        if (tokenType == JwtType.REFRESH_TOKEN)
            return getRefreshJwt(memberId, currentTimeMillis);
        else if (tokenType == JwtType.ACCESS_TOKEN)
            return getAccessJwt(memberId, currentTimeMillis);
        else
            throw new BaseException(UNSUPPORTED_TOKEN_TYPE);
    }

    private String getRefreshJwt(String memberId, Long currentTimeMillis) {
        return Jwts.builder()
            .issuer(issuer)
            .subject(memberId)
            .claim("tokenType", JwtType.REFRESH_TOKEN.getData())
            .issuedAt(new Date(currentTimeMillis))
            .expiration(new Date(currentTimeMillis + refreshExpiredTime))
            .signWith(refreshSecretKey).compact();
    }

    private String getAccessJwt(String memberId, Long currentTimeMillis) {
        return Jwts.builder()
            .issuer(issuer)
            .subject(memberId)
            .claim("tokenType", JwtType.ACCESS_TOKEN.getData())
            .issuedAt(new Date(currentTimeMillis))
            .expiration(new Date(currentTimeMillis + accessExpiredTime))
            .signWith(accessSecretKey).compact();
    }

    public String getMemberId(String token, JwtType jwtType) {
        try {
            if (jwtType == JwtType.REFRESH_TOKEN)
                return Jwts.parser().verifyWith(refreshSecretKey).build().parseSignedClaims(token).getPayload().getSubject();
            else if (jwtType == JwtType.ACCESS_TOKEN)
                return Jwts.parser().verifyWith(accessSecretKey).build().parseSignedClaims(token).getPayload().getSubject();
            else
                throw new BaseException(UNSUPPORTED_TOKEN_TYPE);
        } catch (SignatureException e) {
            throw new BaseException(TOKEN_DECODE_FAILED);
        }
    }

    public Boolean checkJwt(String token, JwtType jwtType) {
        SecretKey secretKey = null;
        if (jwtType == JwtType.REFRESH_TOKEN) {
            secretKey = refreshSecretKey;
        } else if (jwtType == JwtType.ACCESS_TOKEN) {
            secretKey = accessSecretKey;
        } else {
            throw new BaseException(UNSUPPORTED_TOKEN_TYPE);
        }
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_TOKEN);
        } catch (SecurityException | MalformedJwtException e) {
            throw new BaseException(INVALID_JWT);
        } catch (UnsupportedJwtException e) {
            throw new BaseException(UNSUPPORTED_JWT);
        }
        return true;
    }
}
