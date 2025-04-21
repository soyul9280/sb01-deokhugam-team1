package com.codeit.duckhu.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.service.NotificationService;
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
}
