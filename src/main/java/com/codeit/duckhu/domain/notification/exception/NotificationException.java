package com.codeit.duckhu.domain.notification.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {

  private final Instant timestamp;
  private final NotificationErrorCode errorCode;
  private final Map<String, Object> details;

  public NotificationException(NotificationErrorCode errorCode) {
    this(errorCode, new HashMap<>());
  }

  public NotificationException(NotificationErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.timestamp = Instant.now();
    this.errorCode = errorCode;
    this.details = details;
  }
}
