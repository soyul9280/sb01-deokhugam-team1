package com.codeit.duckhu.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(UserException e) {
        UserErrorResponse error=new UserErrorResponse(
                e.getErrorCode().toString(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getTypeName(),
                e.getErrorCode().getStatus().value()
        );
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(error);
    }
}
