package com.codeit.duckhu.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codeit.duckhu.notification.entity.Notification;
import com.codeit.duckhu.notification.repository.NotificationRepsitory;
import com.codeit.duckhu.notification.service.impl.NotificationServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	@Mock
	private NotificationRepsitory notificationRepository;

	@InjectMocks
	private NotificationServiceImpl notificationService;

	@Test
	void 내가_작성한_리뷰에_좋아요가_달리면_알림이_생성된다() {
		// given: 리뷰 ID, 좋아요 누른 사람 ID(triggerUserId) 여기에서 실제로는
		UUID reviewId = UUID.randomUUID();  // 댓글이 달린 리뷰 ID
		UUID triggerUserId = UUID.randomUUID(); // 댓글 작성자 (알림을 발생시킨 사용자)
		UUID receiverId = UUID.randomUUID(); // 리뷰 작성자 (알림을 받는 사용자)

		// when: 서비스 메서드 호출(content는 service에서 생성)
		// 추가로 review를 mock으로 가져와 검증 및 review의 content를 reviewTitle로 변환
		// notification의 content는 "[buzz]님이 나의 리뷰를 좋아합니다." 형식으로 service에서 생성
		// 프론트로 반환할때 userid로 usernickname도 가져와야된다
		Notification result = notificationService.createNotifyByLike(reviewId,triggerUserId,receiverId);

		// then: NotificationRepository.save() 호출 확인 + 검증
		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getReceiverId()).isEqualTo(receiverId);
		assertThat(result.getTriggerUserId()).isEqualTo(triggerUserId);
		assertThat(result.getContent()).contains("님이 나의 리뷰를 좋아합니다.");

		verify(notificationRepository, times(1)).save(any(Notification.class));
	}

	@Test
	void 내가_작성한_리뷰에_댓글이_달리면_알림이_생성된다() {
		// given: 리뷰 ID, 댓글 작성자 ID, 리뷰 작성자 ID
		UUID reviewId = UUID.randomUUID();  // 댓글이 달린 리뷰 ID
		UUID triggerUserId = UUID.randomUUID(); // 댓글 작성자 (알림을 발생시킨 사용자)
		UUID receiverId = UUID.randomUUID(); // 리뷰 작성자 (알림을 받는 사용자)

		// when: 댓글 작성자가 리뷰에 댓글을 남겼을 때 알림이 생성되는 상황을 시뮬레이션
		Notification result = notificationService.createNotifyByComment(reviewId, triggerUserId, receiverId);

		// then: NotificationRepository.save() 호출 확인 및 검증
		assertThat(result.getReviewId()).isEqualTo(reviewId);
		assertThat(result.getTriggerUserId()).isEqualTo(triggerUserId);
		assertThat(result.getReceiverId()).isEqualTo(receiverId);
		assertThat(result.getContent()).contains("님이 나의 리뷰에 댓글을 남겼습니다.");

		verify(notificationRepository, times(1)).save(any(Notification.class));
	}
}