package com.codeit.duckhu.domain.notification.service;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import java.util.UUID;

import com.codeit.duckhu.domain.notification.entity.Notification;

public interface NotificationService {
	NotificationDto createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId);

	NotificationDto createNotifyByComment(UUID reviewId, UUID triggerUserId, UUID receiverId);

	NotificationDto updateConfirmedStatus(UUID notificationId, UUID receiverId, boolean confirmed);
}
