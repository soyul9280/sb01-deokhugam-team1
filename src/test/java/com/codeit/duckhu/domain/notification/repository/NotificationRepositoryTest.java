package com.codeit.duckhu.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.config.JpaConfig;
import com.codeit.duckhu.domain.notification.entity.Notification;
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
            String content = "[buzz]님이 나의 리뷰를 좋아합니다.";

            Notification notification = new Notification(reviewId, receiverId, triggerUserId, content);

            // when
            Notification saved = notificationRepository.save(notification);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getContent()).isEqualTo(content);
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
            String comment = "12345";
            String content = "[buzz]님이 나의 리뷰에 댓글을 남겼습니다.\n" + comment;

            Notification notification = new Notification(reviewId, receiverId, triggerUserId, content);

            // when
            Notification saved = notificationRepository.save(notification);

            // then
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getContent()).isEqualTo(content);
            assertThat(saved.getReviewId()).isEqualTo(reviewId);
            assertThat(saved.getReceiverId()).isEqualTo(receiverId);
            assertThat(saved.getTriggerUserId()).isEqualTo(triggerUserId);
            assertThat(saved.isConfirmed()).isFalse();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }
    }
}
