package com.codeit.duckhu.notification.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {

	//사용자
	@Transactional
	void createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId) {

	}
}
