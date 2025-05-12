package com.codeit.duckhu.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 커스텀 예외 클래스. */
@Getter
@RequiredArgsConstructor
public class DomainException extends RuntimeException {

  private final ErrorCode errorCode;

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }

  public String getCustomException() {
    return errorCode.getMessage() + "in Custom Exception";
  }
}
