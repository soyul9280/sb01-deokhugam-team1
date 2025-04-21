package com.codeit.duckhu.domain.user.exception;


import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
public class UserException extends RuntimeException {
    private final Instant timestamp;
    private final UserErrorCode errorCode;
    private final Map<String,Object> details;

    public UserException(Instant timestamp, UserErrorCode errorCode,Map<String,Object> details) {
        super(errorCode.getMessage());
        this.timestamp = timestamp;
        this.errorCode=errorCode;
        this.details=details;
    }
}
