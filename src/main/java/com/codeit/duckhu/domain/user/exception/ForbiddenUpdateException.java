package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class ForbiddenUpdateException extends UserException {

  public ForbiddenUpdateException(ErrorCode errorCode) {
    super(errorCode);
  }
}
