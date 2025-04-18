package com.codeit.duckhu.domain.user.exception;


import java.time.Instant;
import java.util.Map;

public class EmailDuplicateException extends UserException {

    public EmailDuplicateException(String email) {
        super(Instant.now(),UserErrorCode.EMAIL_DUPLICATION,Map.of("email",email));
    }
}
