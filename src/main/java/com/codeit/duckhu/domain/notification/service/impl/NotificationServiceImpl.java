package com.codeit.duckhu.domain.notification.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.repository.NotificationRepsitory;
import com.codeit.duckhu.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepsitory notificationRepsitory;

	/**
	 * 내가 작성한 리뷰에 다른 사용자가 좋아요를 누르면 알림을 생성한다.
	 *
	 * @param reviewId        알림 대상 리뷰 ID
	 * @param triggerUserId   좋아요를 누른 사용자 ID
	 * @param receiverId      알림을 받을 사용자 ID (리뷰 작성자)
	 * @return 생성된 알림 객체
	 */
	@Override
	@Transactional
	public Notification createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId) {
		// Todo 실제 triggerNickname,receiverid는 각각 review, User에서 조회할수 있어야 된다(리팩토링)
		String content = generateLikeMessage();

		// 알림 객체 생성
		Notification notification = new Notification(
			reviewId,
			receiverId,
			triggerUserId,
			content
		);
		return notificationRepsitory.save(notification);
	}

	/**
	 * 내가 작성한 리뷰에 다른 사용자가 댓글을 남기면 알림을 생성한다.
	 *
	 * @param reviewId        알림 대상 리뷰 ID
	 * @param triggerUserId   댓글을 작성한 사용자 ID
	 * @param receiverId      알림을 받을 사용자 ID (리뷰 작성자)
	 * @return 생성된 알림 객체
	 */
	@Override
	@Transactional
	public Notification createNotifyByComment(UUID reviewId, UUID triggerUserId, UUID receiverId) {
		// 리팩터링 대상: 실제 닉네임, 댓글 내용 포함 가능
		// Todo 실제 triggerNickname,comment, receiverid는 각각 review, User에서 조회할수 있어야 된다(리팩토링)
		String content = generateCommentMessage();

		// 알림 객체 생성
		Notification notification = new Notification(
			reviewId,
			receiverId,
			triggerUserId,
			content
		);

		return notificationRepsitory.save(notification);
	}

	// 내부 메시지 생성 메서드
	private static String generateLikeMessage() {
		return "[닉네임] 님이 나의 리뷰를 좋아합니다.";
	}
	private static String generateLikeMessage(String nickname) {
		return nickname + "님이 나의 리뷰를 좋아합니다.";
	}


	private static String generateCommentMessage() {
		return "님이 나의 리뷰에 댓글을 남겼습니다.";
	}
	private static String generateCommentMessage(String nickname, String comment) {
		return nickname + "님이 나의 리뷰에 댓글을 남겼습니다." + comment;
	}
}
