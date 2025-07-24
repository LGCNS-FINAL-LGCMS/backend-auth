package com.lgcms.auth.common.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthError implements ErrorCodeInterface {
    UNSUPPORTED_TOKEN_TYPE("AUTH_001", "지원하는 토큰 타입이 아닙니다.", HttpStatus.NOT_FOUND),
    TOKEN_DECODE_FAILED("AUTH_002", "토큰 복호화 실패", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN("AUTH_003", "토큰이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_JWT("AUTH_004", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_JWT("AUTH_005", "지원하지 않는 토큰입니다.", HttpStatus.BAD_REQUEST),
    NO_SUCH_SOCIAL_TYPE("AUTH_006", "지원하는 소셜 로그인 타입이 아닙니다.", HttpStatus.NOT_FOUND),
    FAILED_GOOGLE_SOCIAL_LOGIN("AUTH_007", "구글 소셜 로그인에 실패했습니다.", HttpStatus.BAD_REQUEST),
    MEMBER_SERVER_ERROR("AUTH_008", "회원 서버 통신 오류", HttpStatus.I_AM_A_TEAPOT),
    USED_REFRESH_TOKEN("AUTH_009", "사용할 수 없는 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),
    ;
    private final String status;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.builder()
            .status(status)
            .message(message)
            .httpStatus(httpStatus)
            .build();
    }
}
