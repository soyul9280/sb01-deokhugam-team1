package com.codeit.duckhu.domain.book.exception;

import com.codeit.duckhu.global.exception.CustomException;
import com.codeit.duckhu.global.exception.ErrorCode;

public class BookException extends CustomException {

  public BookException(ErrorCode errorCode) {
    super(errorCode);
  }
}
