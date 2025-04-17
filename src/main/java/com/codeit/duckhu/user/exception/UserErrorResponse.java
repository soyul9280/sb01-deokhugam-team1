package com.codeit.duckhu.user.exception;


import java.time.Instant;
import java.util.Map;

public class UserErrorResponse {
    private Instant timestamp;
    private String code;
    private String message;
    private Map<String,Object> details;
    private String exceptionType;
    private int status;

    public UserErrorResponse(String code, String message, Map<String, Object> details, String exceptionType, int status) {
        this.timestamp = Instant.now();
        this.code = code;
        this.message = message;
        this.details = details;
        this.exceptionType = exceptionType;
        this.status = status;
    }
}