package com.codeit.duckhu.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.service.NotificationService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepsitory notificationRepository;

    @Nested
    @DisplayName("알림 저장")
    class SaveNotificationTest {

        @Test
        @DisplayName("사용자가 다른 사람의 리뷰에 좋아요를 누르면 알림이 저장된다")
        void createsNotificationForOtherUsersReviewLike() {
            // given
            UUID reviewId = UUID.randomUUID();
            UUID triggerUserId = UUID.randomUUID();
            UUID receiverId = UUID.randomUUID();
            String nickname = "buzz";

            Notification notification = Notification.forLike(reviewId, receiverId, triggerUserId,
                nickname);

            // when
            Notification saved = notificationRepository.save(notification);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getContent()).isEqualTo("[buzz]님이 나의 리뷰를 좋아합니다.");
            assertThat(saved.getReviewId()).isEqualTo(reviewId);
            assertThat(saved.getReceiverId()).isEqualTo(receiverId);
            assertThat(saved.getTriggerUserId()).isEqualTo(triggerUserId);
            assertThat(saved.isConfirmed()).isFalse();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("사용자가 다른 사람의 리뷰에 댓글을 작성하면 알림이 저장된다")
        void createsNotificationForOtherUsersReviewComment() {
            // given
            UUID reviewId = UUID.randomUUID();
            UUID triggerUserId = UUID.randomUUID();
            UUID receiverId = UUID.randomUUID();
            String nickname = "buzz";
            String comment = "12345";

            Notification notification = Notification.forComment(reviewId, receiverId, triggerUserId,
                nickname, comment);

            // when
            Notification saved = notificationRepository.save(notification);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getContent()).isEqualTo("[buzz]님이 나의 리뷰에 댓글을 남겼습니다.\n12345");
            assertThat(saved.getReviewId()).isEqualTo(reviewId);
            assertThat(saved.getReceiverId()).isEqualTo(receiverId);
            assertThat(saved.getTriggerUserId()).isEqualTo(triggerUserId);
            assertThat(saved.isConfirmed()).isFalse();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("알림 조회")
    class FindNotificationTest {

        @Test
        @DisplayName("사용자의 모든 알림을 조회한다")
        void findAllByReceiverId() {
            // Given
            UUID receiverId = UUID.randomUUID();
            Notification n1 = Notification.forLike(UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz");
            Notification n2 = Notification.forComment(UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz", "댓글");
            Notification n3 = Notification.forLike(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "buzz"); // 다른 수신자

            notificationRepository.saveAll(List.of(n1, n2, n3));

            // When
            List<Notification> result = notificationRepository.findAllByReceiverId(receiverId);

            // Then
            assertThat(result).hasSize(2)
                .allMatch(n -> n.getReceiverId().equals(receiverId));
        }
    }

    @Nested
    @DisplayName("알림 삭제")
    class DeleteNotificationTest {

        @Test
        @DisplayName("1주일이 지난 확인된 알림은 삭제된다")
        void deleteOldConfirmedNotifications() {
            // given: 알림 3개 생성 (1개는 8일 전, 나머지는 최근 생성 or 미확인 상태)
            UUID receiverId = UUID.randomUUID();

            Notification oldConfirmed = Notification.forLike(UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz");
            oldConfirmed.markAsConfirmed();
            // 8일 전으로 updatedAt 강제 설정
            ReflectionTestUtils.setField(oldConfirmed, "updatedAt", Instant.now().minus(8, ChronoUnit.DAYS));

            Notification recentConfirmed = Notification.forLike(UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz");
            recentConfirmed.markAsConfirmed();

            Notification unconfirmed = Notification.forLike(UUID.randomUUID(), receiverId, UUID.randomUUID(), "buzz");

            notificationRepository.saveAll(List.of(oldConfirmed, recentConfirmed, unconfirmed));

            // when: 삭제 실행 (현재 미구현 상태)
            Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
            notificationRepository.deleteOldConfirmedNotifications(cutoff);

            // then: oldConfirmed만 삭제되고 나머지는 살아있어야 함
            List<Notification> remaining = notificationRepository.findAll();
            assertThat(remaining).containsExactlyInAnyOrder(recentConfirmed, unconfirmed);
        }
    }
}
