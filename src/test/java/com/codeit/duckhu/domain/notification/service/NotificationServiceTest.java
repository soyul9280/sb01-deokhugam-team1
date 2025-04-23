package com.codeit.duckhu.domain.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.exception.NotificationNotFoundException;
import com.codeit.duckhu.domain.notification.mapper.NotificationMapper;
import com.codeit.duckhu.domain.notification.repository.NotificationRepository;
import com.codeit.duckhu.domain.notification.service.impl.NotificationServiceImpl;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID reviewId;
    private UUID triggerUserId;
    private UUID receiverId;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID(); // 댓글이 달린 리뷰 ID
        triggerUserId = UUID.randomUUID(); // 댓글 or 좋아요를 누른 사용자
        receiverId = UUID.randomUUID(); // 리뷰 작성자 (알림 수신자)
    }

    @Nested
    @DisplayName("알림 생성")
    class CreateNotificationTest {

        @Test
        @DisplayName("리뷰 좋아요 알림 생성 성공")
        void createsNotificationForLike() {
            // given: 리뷰 내용과 좋아요 누른 사용자의 닉네임을 준비
            String nickname = "buzz";
            String reviewContent = "리뷰 내용";

            // 리뷰, 리뷰 작성자(수신자), 좋아요 누른 사용자(트리거 유저)를 mock으로 설정
            Review review = mock(Review.class);
            User receiver = mock(User.class);
            User triggerUser = mock(User.class);

            // 리뷰 객체가 리턴할 사용자(수신자)의 ID와 리뷰 내용을 정의
            given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

            given(review.getUser()).willReturn(receiver);
            given(receiver.getId()).willReturn(receiverId);
            given(review.getContent()).willReturn(reviewContent);

            // 좋아요를 누른 유저의 닉네임 설정
            given(triggerUser.getNickname()).willReturn(nickname);

            given(userRepository.findById(triggerUserId))
                .willReturn(Optional.of(triggerUser));

            // 서비스 내부에서 생성할 알림 객체
            Notification notification =
                Notification.forLike(reviewId, receiverId, nickname, reviewContent);

            // 알림 저장 후 매퍼를 통해 변환된 DTO 예상 결과
            NotificationDto expectedDto = new NotificationDto(UUID.randomUUID(), receiverId,
                reviewId, reviewContent, notification.getContent(), false, Instant.now(),
                Instant.now());

            // Mock repository 동작 설정
            given(notificationRepository.save(any(Notification.class)))
                .willReturn(notification);
            given(notificationMapper.toDto(notification))
                .willReturn(expectedDto);

            // when: 좋아요 알림 생성 서비스 호출
            NotificationDto result = notificationService.createNotifyByLike(reviewId,
                triggerUserId);

            // then: 반환값 검증 및 repository, mapper 호출 여부 검증
            assertThat(result.reviewId()).isEqualTo(reviewId);
            assertThat(result.userId()).isEqualTo(receiverId);
            assertThat(result.content()).isEqualTo(notification.getContent());

            then(reviewRepository).should().findById(reviewId);
            then(userRepository).should().findById(triggerUserId);
            then(notificationRepository).should().save(any(Notification.class));
            then(notificationMapper).should().toDto(notification);
        }

        @Test
        @DisplayName("리뷰 댓글 알림 생성 성공")
        void createsNotificationForComment() {
            // given: 댓글 작성자의 닉네임, 댓글 내용, 리뷰 내용을 정의
            String nickname = "buzz";
            String comment = "이 리뷰 좋네요!";
            String reviewContent = "맛있었어요.";

            // 리뷰, 수신자(리뷰 작성자), 댓글 작성자(mock)를 설정
            Review review = mock(Review.class);
            User receiver = mock(User.class);
            User triggerUser = mock(User.class);

            // 리뷰가 참조하는 작성자와 그 ID, 리뷰 내용을 정의
            given(reviewRepository.findById(reviewId))
                .willReturn(Optional.of(review));

            given(review.getUser()).willReturn(receiver);
            given(receiver.getId()).willReturn(receiverId);
            given(review.getContent()).willReturn(reviewContent);

            // 댓글 작성자(트리거 유저)의 닉네임 정의
            given(userRepository.findById(triggerUserId))
                .willReturn(Optional.of(triggerUser));
            given(triggerUser.getNickname()).willReturn(nickname);

            // 알림 객체 생성 (댓글용)
            Notification notification =
                Notification.forComment(reviewId, receiverId, nickname, comment, reviewContent);

            // 매핑 후 기대되는 DTO 정의
            NotificationDto expectedDto = new NotificationDto(UUID.randomUUID(), receiverId,
                reviewId, reviewContent, notification.getContent(), false, Instant.now(),
                Instant.now());

            // Mock repository 설정
            given(notificationRepository.save(any(Notification.class))).willReturn(notification);
            given(notificationMapper.toDto(notification)).willReturn(expectedDto);

            // when: 댓글 알림 생성 서비스 호출
            NotificationDto result =
                notificationService.createNotifyByComment(reviewId, triggerUserId, comment);

            // then: 결과 및 각 mock 객체의 호출 여부 검증
            assertThat(result.reviewId()).isEqualTo(reviewId);
            assertThat(result.userId()).isEqualTo(receiverId);
            assertThat(result.content()).isEqualTo(notification.getContent());

            then(reviewRepository).should().findById(reviewId);
            then(userRepository).should().findById(triggerUserId);
            then(notificationRepository).should().save(any(Notification.class));
            then(notificationMapper).should().toDto(notification);
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
            Notification notification =
                Notification.forLike(UUID.randomUUID(), receiverId, "buzz", "타이틀");

            NotificationDto expectedDto = new NotificationDto(
                notificationId,
                receiverId,
                notification.getReviewId(),
                "타이틀",
                notification.getContent(),
                true,
                Instant.now(),
                Instant.now());

            given(notificationRepository.findById(notificationId)).willReturn(
                Optional.of(notification));
            given(notificationMapper.toDto(notification)).willReturn(expectedDto);

            // when
            NotificationDto result =
                notificationService.updateConfirmedStatus(notificationId, receiverId, true);

            // then
            assertThat(notification.isConfirmed()).isTrue();
            assertThat(result.confirmed()).isTrue();
            then(notificationRepository).should(times(1)).findById(notificationId);
            then(notificationMapper).should(times(1)).toDto(notification);
        }

        @Test
        @DisplayName("존재하지 않는 알림 ID로 조회 시 예외가 발생한다")
        void throwsNotificationNotFoundExceptionIfNotFound() {
            UUID notificationId = UUID.randomUUID();
            given(notificationRepository.findById(notificationId))
                .willReturn(Optional.empty());

            assertThatThrownBy(() ->
                notificationService.updateConfirmedStatus(notificationId, receiverId, true))
                .isInstanceOf(NotificationNotFoundException.class);

            then(notificationRepository).should(times(1)).findById(notificationId);
        }

        @Test
        @DisplayName("사용자의 모든 알림을 읽음 처리한다")
        void updateAllNotificationsConfirmedSuccessfully() {
            Notification n1 = Notification.forLike(reviewId, receiverId, "a", "t1");
            Notification n2 = Notification.forComment(reviewId, receiverId, "b", "c", "t2");
            Notification n3 = Notification.forLike(reviewId, receiverId, "c", "t3");
            List<Notification> list = List.of(n1, n2, n3);

            given(notificationRepository.findAllByReceiverId(receiverId))
                .willReturn(list);

            notificationService.updateAllConfirmedStatus(receiverId);

            assertThat(list).allMatch(Notification::isConfirmed);
            then(notificationRepository).should(times(1)).findAllByReceiverId(receiverId);
        }
    }

    @Nested
    @DisplayName("알림 삭제")
    class DeleteNotificationTest {

        @Test
        @DisplayName("1주일 지난 확인된 알림 삭제")
        void deleteConfirmedNotificationsOlderThanAWeek() {
            Instant before = Instant.now().minus(7, ChronoUnit.DAYS);
            notificationService.deleteConfirmedNotificationsOlderThanAWeek();

            then(notificationRepository).should(times(1))
                .deleteOldConfirmedNotifications(argThat(cutoff ->
                    !cutoff.isAfter(Instant.now()) &&
                        !cutoff.isBefore(before.minus(1, ChronoUnit.SECONDS))
                ));
        }
    }
}
