package com.codeit.duckhu.domain.user.exception;


import com.codeit.duckhu.global.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;

public class InvalidLoginException extends UserException{

    public InvalidLoginException(ErrorCode errorCode) {
        super(errorCode);
    }
}
