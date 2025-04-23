package com.codeit.duckhu.domain.notification.exception;

import java.util.Map;
import java.util.UUID;

public class NotificationAccessDeniedException extends NotificationException {

  public NotificationAccessDeniedException(UUID userId, UUID notificationId) {
    super(
        NotificationErrorCode.INVALID_NOTIFICATION_RECEIVER,
        Map.of(
            "userId", userId.toString(),
            "notificationId", notificationId.toString()));
  }
}
