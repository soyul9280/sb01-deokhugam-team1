package com.codeit.duckhu.domain.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.exception.NotificationNotFoundException;
import com.codeit.duckhu.domain.notification.mapper.NotificationMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.repository.NotificationRepsitory;
import com.codeit.duckhu.domain.notification.service.impl.NotificationServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepsitory notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID reviewId;
    private UUID triggerUserId;
    private UUID receiverId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();       // 댓글이 달린 리뷰 ID
        triggerUserId = UUID.randomUUID();  // 댓글 or 좋아요를 누른 사용자
        receiverId = UUID.randomUUID();     // 리뷰 작성자 (알림 수신자)
    }

    @Nested
    @DisplayName("알림 생성")
    class CreateNotificationTest {

        @Test
        @DisplayName("사용자가 다른 사람의 리뷰에 좋아요를 누르면 알림이 생성된다")
        void createsNotificationForOtherUsersReviewLike() {
            // given: 리뷰 ID, 좋아요 누른 사람 ID(triggerUserId) BeforeEach로 미리 구현
            String expectedNickname = "buzz";
            String expectedContent = "[" + expectedNickname + "]님이 나의 리뷰를 좋아합니다.";

            //// 알림 저장 시 repository에서 반환할 예상 결과 정의
            Notification expectedNotification = Notification.forLike(reviewId, receiverId,
                triggerUserId, expectedNickname);

            NotificationDto expectedDto = new NotificationDto(
                UUID.randomUUID(),
                receiverId,
                reviewId,
                null, // reviewTitle은 현재 null 처리
                expectedContent,
                false,
                Instant.now(),
                Instant.now()
            );

            // when: 좋아요 발생 시 알림 생성 서비스 호출 (실제 메시지 생성은 서비스 내부 로직)
            // 추가로 review를 mock으로 가져와 검증 및 review의 content를 reviewTitle로 변환
            // notification의 content는 "[buzz]님이 나의 리뷰를 좋아합니다." 형식으로 service에서 생성
            // 프론트로 반환할때 userid로 usernickname도 가져와야된다
            given(notificationRepository.save(any(Notification.class))).willReturn(
                expectedNotification);
            given(notificationMapper.toDto(any(Notification.class))).willReturn(expectedDto);

            NotificationDto result = notificationService.createNotifyByLike(reviewId, triggerUserId,
                receiverId);

            // then: NotificationRepository.save() 호출 확인 + 검증
            assertThat(result.reviewId()).isEqualTo(reviewId);
            assertThat(result.userId()).isEqualTo(receiverId);
            assertThat(result.content()).isEqualTo(expectedContent);
            assertThat(result.confirmed()).isFalse(); // 확인 상태는 기본으로 false이다

            then(notificationRepository).should(times(1)).save(any(Notification.class));
            then(notificationMapper).should(times(1)).toDto(expectedNotification);
        }

        @Test
        @DisplayName("사용자가 다른 사람의 리뷰에 댓글을 작성하면 알림이 생성된다")
        void createsNotificationForOtherUsersReviewComment() {
            // given: 리뷰 ID, 댓글 작성자 ID, 리뷰 작성자 ID BeforeEach로 미리 구현
            String nickname = "buzz";
            String comment = "12345";
            String expectedContent = "[" + nickname + "]님이 나의 리뷰에 댓글을 남겼습니다.\n" + comment;
            Notification expectedNotification = Notification.forComment(reviewId, receiverId,
                triggerUserId, nickname, comment);

            NotificationDto expectedDto = new NotificationDto(UUID.randomUUID(), receiverId,
                reviewId, null, expectedContent, false, Instant.now(), Instant.now());

            // when: 댓글 작성자가 리뷰에 댓글을 남겼을 때 알림이 생성되는 상황을 시뮬레이션
            given(notificationRepository.save(any(Notification.class))).willReturn(
                expectedNotification);
            given(notificationMapper.toDto(any(Notification.class))).willReturn(expectedDto);

            NotificationDto result = notificationService.createNotifyByComment(reviewId,
                triggerUserId, receiverId);

            // then: NotificationRepository.save() 호출 확인 및 검증
            assertThat(result.reviewId()).isEqualTo(reviewId);
            assertThat(result.userId()).isEqualTo(receiverId);
            assertThat(result.content()).isEqualTo(expectedContent);
            assertThat(result.confirmed()).isFalse();

            then(notificationRepository).should(times(1)).save(any(Notification.class));
            then(notificationMapper).should(times(1)).toDto(expectedNotification);
        }

        // Todo: 내가 작성한 리뷰의 인기 순위가 각 기간 별 10위 내에 선정되면 알림이 생성됩니다.
    }

    @Nested
    @DisplayName("알림 읽은 상태 업데이트")
    class UpdateNotificationTest {

        @Test
        @DisplayName("알림 ID에 해당하는 알림의 읽음 상태를 true로 업데이트한다")
        void updateNotificationConfirmedSuccessfully() {
            // given
            UUID notificationId = UUID.randomUUID();
            UUID receiverId = UUID.randomUUID();

            Notification notification = Notification.forLike(
                UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz"
            );

            NotificationDto expectedDto = new NotificationDto(
                notificationId,
                receiverId,
                notification.getReviewId(),
                null,
                notification.getContent(),
                true,
                Instant.now(),
                Instant.now()
            );

            given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));
            given(notificationMapper.toDto(notification)).willReturn(expectedDto);

            // when
            NotificationDto result = notificationService.updateConfirmedStatus(notificationId, receiverId, true);

            // then
            assertThat(notification.isConfirmed()).isTrue();
            assertThat(result.confirmed()).isTrue();
            then(notificationRepository).should(times(1)).findById(notificationId);
            then(notificationMapper).should(times(1)).toDto(notification);
        }

        @Test
        @DisplayName("존재하지 않는 알림 ID로 조회 시 예외가 발생한다")
        void throwsNotificationNotFoundExceptionIfNotFound() {
            // given
            UUID notificationId = UUID.randomUUID();
            UUID receiverId = UUID.randomUUID();

            given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                notificationService.updateConfirmedStatus(notificationId, receiverId, true)
            ).isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining("해당 알림을 찾을 수 없습니다.");

            then(notificationRepository).should(times(1)).findById(notificationId);
        }
    }
}