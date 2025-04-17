package com.codeit.duckhu.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다.");

    private final HttpStatus status;
    private final String message;
}
