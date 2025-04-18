package com.codeit.duckhu.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode {
    EMAIL_DUPLICATION("USER_409","이미 존재하는 이메일입니다.");

    private final String code;
    private final String message;
}
