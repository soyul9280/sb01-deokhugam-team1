package com.codeit.duckhu.domain.notification.service;

import com.codeit.duckhu.domain.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import java.time.Instant;
import java.util.UUID;

public interface NotificationService {
  NotificationDto createNotifyByLike(UUID reviewId, UUID triggerUserId);

  NotificationDto createNotifyByComment(UUID reviewId, UUID triggerUserId, String comment);

  NotificationDto updateConfirmedStatus(UUID notificationId, UUID receiverId, boolean confirmed);

  void updateAllConfirmedStatus(UUID receiverId);

  // 사용자가 확인을 한 일주일을 지난 알림들 삭제
  void deleteConfirmedNotificationsOlderThanAWeek();

  // 목록 조회를 위한 메서드
  CursorPageResponseNotificationDto getNotifications(UUID receiverId, String direction, Instant cursor, int limit);
}
