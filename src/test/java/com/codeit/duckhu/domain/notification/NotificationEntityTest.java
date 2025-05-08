package com.codeit.duckhu.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.global.type.PeriodType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class NotificationEntityTest {

    @Test
    @DisplayName("forPopularReview DAILY 분기")
    void popularReviewDaily() {
        UUID reviewId = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        Notification notif = Notification.forPopularReview(
            reviewId, receiver, PeriodType.DAILY, 1, "제목"
        );

        assertThat(notif.getContent())
            .contains("일간 인기 리뷰 1위에 선정되었습니다.");
    }

    @Test
    @DisplayName("forPopularReview WEEKLY 분기")
    void popularReviewWeekly() {
        UUID reviewId = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        Notification notif = Notification.forPopularReview(
            reviewId, receiver, PeriodType.WEEKLY, 2, "제목"
        );

        assertThat(notif.getContent())
            .contains("주간 인기 리뷰 2위에 선정되었습니다.");
    }

    @Test
    @DisplayName("forPopularReview MONTHLY 분기")
    void popularReviewMonthly() {
        UUID reviewId = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        Notification notif = Notification.forPopularReview(
            reviewId, receiver, PeriodType.MONTHLY, 3, "제목"
        );

        assertThat(notif.getContent())
            .contains("월간 인기 리뷰 3위에 선정되었습니다.");
    }

    @Test
    @DisplayName("forPopularReview ALL_TIME 분기")
    void popularReviewAllTime() {
        UUID reviewId = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        Notification notif = Notification.forPopularReview(
            reviewId, receiver, PeriodType.ALL_TIME, 4, "제목"
        );

        assertThat(notif.getContent())
            .contains("전체 인기 리뷰 4위에 선정되었습니다.");
    }
}
