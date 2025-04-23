package com.codeit.duckhu.domain.notification.exception;

import java.util.Map;
import java.util.UUID;

public class NotificationNotFoundException extends NotificationException {

  public NotificationNotFoundException(UUID notificationId) {
    super(
        NotificationErrorCode.NOTIFICATION_NOT_FOUND,
        Map.of("notificationId", notificationId.toString()));
  }
}
