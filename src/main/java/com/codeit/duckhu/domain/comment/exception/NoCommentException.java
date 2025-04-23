package com.codeit.duckhu.domain.comment.exception;

import com.codeit.duckhu.domain.comment.service.ErrorCode;
import org.springframework.http.HttpStatus;

public class NoCommentException extends RuntimeException {

  private final ErrorCode errorCode;

  public NoCommentException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public HttpStatus getStatus() {
    return errorCode.getStatus();
  }

  public String getDetail() {
    return errorCode.getDetail();
  }
}
