package com.codeit.duckhu.domain.user.exception;

import com.codeit.duckhu.global.exception.CustomException;
import com.codeit.duckhu.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class UserException extends CustomException {

  public UserException(ErrorCode errorCode) {
    super(errorCode);
  }
}
