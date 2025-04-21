package com.codeit.duckhu.domain.notification.service;

import java.util.UUID;

import com.codeit.duckhu.domain.notification.entity.Notification;

public interface NotificationService {
	Notification createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId);

	Notification createNotifyByComment(UUID reviewId, UUID triggerUserId, UUID receiverId);
}
