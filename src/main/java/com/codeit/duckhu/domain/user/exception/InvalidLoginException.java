package com.codeit.duckhu.domain.user.exception;


import java.time.Instant;
import java.util.Map;

public class InvalidLoginException extends UserException{
    public InvalidLoginException(String email) {
        super(Instant.now(),UserErrorCode.LOGIN_INPUT_INVALID, Map.of("email", email));
    }
}
