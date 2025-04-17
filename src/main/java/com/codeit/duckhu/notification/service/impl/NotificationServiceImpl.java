package com.codeit.duckhu.notification.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.duckhu.notification.entity.Notification;
import com.codeit.duckhu.notification.repository.NotificationRepsitory;
import com.codeit.duckhu.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepsitory notificationRepsitory;

	//사용자
	@Override
	@Transactional
	public Notification createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId) {
		String content = "[buzz]님이 나의 리뷰를 좋아합니다."; // Refactor 대상
		Notification notification = new Notification(
			reviewId,
			receiverId,
			triggerUserId,
			content
		);
		notificationRepsitory.save(notification);
		return notification;
	}
}
