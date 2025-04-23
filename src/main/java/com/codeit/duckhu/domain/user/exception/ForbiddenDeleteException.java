package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class ForbiddenDeleteException extends UserException {
  public ForbiddenDeleteException(ErrorCode errorCode) {
    super(errorCode);
  }
}
