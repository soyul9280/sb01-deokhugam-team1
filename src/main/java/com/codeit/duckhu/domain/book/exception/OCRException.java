package com.codeit.duckhu.domain.book.exception;

import com.codeit.duckhu.global.exception.ErrorCode;

public class OCRException extends BookException {

  public OCRException(ErrorCode errorCode) {
    super(errorCode);
  }
}
