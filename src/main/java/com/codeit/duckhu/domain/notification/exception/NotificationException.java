package com.codeit.duckhu.domain.notification.exception;

import com.codeit.duckhu.global.exception.CustomException;
import com.codeit.duckhu.global.exception.ErrorCode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class NotificationException extends CustomException {

  public NotificationException(ErrorCode errorCode) {
    super(errorCode);
  }
}
