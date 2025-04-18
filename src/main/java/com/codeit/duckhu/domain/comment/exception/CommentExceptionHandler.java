package com.codeit.duckhu.domain.comment.exception;

import com.codeit.duckhu.global.response.ErrorResponse;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

  @ExceptionHandler(NoCommentException.class)
  public ResponseEntity<ErrorResponse> NoCommentExceptionHandler(NoCommentException e){
    return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(
        Instant.now(),
        e.getStatus(),
        "잘못된 요청입니다.",
        e.getMessage()
    ));
  }
}
