package com.lgcms.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.lgcms.auth.common.dto.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static com.lgcms.auth.common.dto.exception.AuthError.FAILED_GOOGLE_SOCIAL_LOGIN;

@Slf4j
@Component
public class OauthUtil {
    @Value("${google.audience}")
    private String googleAudience;

    public GoogleIdToken.Payload getSubFromGoogleIdToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(googleAudience))
            .build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            log.info("구글 소셜 로그인 에러");
            throw new BaseException(FAILED_GOOGLE_SOCIAL_LOGIN);
        }
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            log.info("구글 소셜 로그인 에러");
            throw new BaseException(FAILED_GOOGLE_SOCIAL_LOGIN);
        }
    }
}
