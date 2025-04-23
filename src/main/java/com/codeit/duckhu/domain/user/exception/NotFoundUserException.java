package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class NotFoundUserException extends UserException {
  public NotFoundUserException(ErrorCode errorCode) {
    super(errorCode);
  }
}
