package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ForbiddenUpdateException extends UserException{

    public ForbiddenUpdateException(ErrorCode errorCode) {
        super(errorCode);
    }
}

