package com.codeit.duckhu.domain.notification.service;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import java.util.UUID;

import com.codeit.duckhu.domain.notification.entity.Notification;

public interface NotificationService {
	NotificationDto createNotifyByLike(UUID reviewId, UUID triggerUserId);

	NotificationDto createNotifyByComment(UUID reviewId, UUID triggerUserId, String comment);

	NotificationDto updateConfirmedStatus(UUID notificationId, UUID receiverId, boolean confirmed);

	void updateAllConfirmedStatus(UUID receiverId);

	// 사용자가 확인을 한 일주일을 지난 알림들 삭제
	void deleteConfirmedNotificationsOlderThanAWeek();
}
