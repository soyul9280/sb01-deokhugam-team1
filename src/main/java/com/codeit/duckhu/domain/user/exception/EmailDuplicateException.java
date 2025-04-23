package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class EmailDuplicateException extends UserException {
  public EmailDuplicateException(ErrorCode errorCode) {
    super(errorCode);
  }
}
