package com.codeit.duckhu.user.exception;


import java.util.Map;

public class EmailDuplicateException extends UserException {

    public EmailDuplicateException(String email) {
        super(UserErrorCode.EMAIL_DUPLICATION,Map.of("email",email));
    }
}
