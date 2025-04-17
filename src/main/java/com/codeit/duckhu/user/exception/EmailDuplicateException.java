package com.codeit.duckhu.user.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class EmailDuplicateException extends UserException {

    public EmailDuplicateException(String email) {
        super(UserErrorCode.EMAIL_DUPLICATION,Map.of("email",email));
    }
}
