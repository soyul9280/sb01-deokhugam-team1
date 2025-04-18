package com.codeit.duckhu.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String,Object> details= e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        UserErrorResponse error = new UserErrorResponse(
                "InvalidMethodArgumentException",
                "요청 값 검증 실패하였습니다.",
                details,
                e.getClass().getTypeName(),
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserErrorResponse> handleException(Exception e) {
        UserErrorResponse error = new UserErrorResponse(
                "EXCEPTION_500",
                "예기치 않은 오류가 발생했습니다.",
                Collections.emptyMap(),
                e.getClass().getTypeName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
