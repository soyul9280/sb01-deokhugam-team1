package com.codeit.duckhu.domain.comment.exception;


import com.codeit.duckhu.domain.comment.service.ErrorCode;

public class NoCommentException extends RuntimeException {

  public NoCommentException(ErrorCode message) {
    super(message.getMessage());
  }
}
