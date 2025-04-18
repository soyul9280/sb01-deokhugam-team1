package com.codeit.duckhu.comments.exception;


import com.codeit.duckhu.comments.service.ErrorCode;

public class NoCommentException extends RuntimeException {

  public NoCommentException(ErrorCode message) {
    super(message.getMessage());
  }
}
