package com.codeit.duckhu.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	@Test
	void 내가_작성한_리뷰에_좋아요가_달리면_알림이_생성된다() {
		// given: 리뷰 ID, 좋아요 누른 사람 ID(triggerUserId)
		// when: 서비스 메서드 호출
		// then: NotificationRepository.save() 호출 확인
	}

	@Test
	void 내가_작성한_리뷰에_댓글이_달리면_알림이_생성된다() {
		// given: 리뷰 ID, 댓글 작성자 ID, 리뷰 작성자 ID
		// when: 서비스 메서드 호출
		// then: NotificationRepository.save() 호출 확인
	}
}