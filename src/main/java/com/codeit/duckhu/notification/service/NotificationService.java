package com.codeit.duckhu.notification.service;

import java.util.UUID;

import com.codeit.duckhu.notification.entity.Notification;

public interface NotificationService {
	Notification createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId);

	Notification createNotifyByComment(UUID reviewId, UUID triggerUserId, UUID receiverId);
}
