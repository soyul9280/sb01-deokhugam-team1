package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class InvalidLoginException extends UserException {

  public InvalidLoginException(ErrorCode errorCode) {
    super(errorCode);
  }
}
