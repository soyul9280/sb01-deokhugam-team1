package com.codeit.duckhu.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커스텀 예외 클래스.
 */
@Getter
@RequiredArgsConstructor
public class ReviewCustomException extends RuntimeException {

  private final ReviewErrorCode errorCode;

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }

  public String getCustomException() {
    return errorCode.getMessage() + "in Custom Exception";
  }
}
