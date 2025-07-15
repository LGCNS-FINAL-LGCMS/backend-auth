package com.lgcms.auth.common.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthError implements ErrorCodeInterface {
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
