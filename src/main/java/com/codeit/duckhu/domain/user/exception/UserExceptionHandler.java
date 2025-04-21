package com.codeit.duckhu.domain.user.exception;

import org.aspectj.weaver.ast.Not;
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
    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(EmailDuplicateException e) {
        UserErrorResponse error=new UserErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                HttpStatus.CONFLICT.value()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(NotFoundUserException e) {
        UserErrorResponse error=new UserErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(InvalidLoginException e) {
        UserErrorResponse error=new UserErrorResponse(
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage(),
                e.getDetails(),
                e.getClass().getSimpleName(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String,Object> details= e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        UserErrorResponse error = new UserErrorResponse(
                "InvalidMethodArgumentException",
                "요청 값 검증 실패하였습니다.",
                details,
                e.getClass().getSimpleName(),
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
                e.getClass().getSimpleName(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
