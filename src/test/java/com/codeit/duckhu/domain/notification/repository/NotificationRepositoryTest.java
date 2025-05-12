package com.codeit.duckhu.domain.notification.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.review.repository.TestJpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:notifications.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import({TestJpaConfig.class, NotificationRepositoryImpl.class})
public class NotificationRepositoryTest {

  @Autowired private NotificationRepository notificationRepository;

  @PersistenceContext private EntityManager em;

  private UUID seededBookId;
  private UUID seededUserId;
  private UUID seededReviewId;

  @BeforeEach
  void initSeedIds() {
    seededBookId   = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    seededUserId   = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    seededReviewId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
  }

  @Nested
  @DisplayName("알림 저장")
  class SaveNotificationTest {

    @Test
    @DisplayName("사용자가 다른 사람의 리뷰에 좋아요를 누르면 알림이 저장된다")
    void createsNotificationForOtherUsersReviewLike() {
      // given
      UUID reviewId = UUID.randomUUID();
      UUID receiverId = UUID.randomUUID();
      String nickname = "buzz";
      String reviewTitle = "가나다";

      Notification notification = Notification.forLike(reviewId, receiverId, nickname, reviewTitle);

      // when
      Notification saved = notificationRepository.save(notification);

      // then
      assertThat(saved.getId()).isNotNull();
      assertThat(saved.getContent()).isEqualTo("[buzz]님이 나의 리뷰를 좋아합니다.");
      assertThat(saved.getReviewId()).isEqualTo(reviewId);
      assertThat(saved.getReceiverId()).isEqualTo(receiverId);
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
      String reviewTitle = "~책에 대한 리뷰";
      String comment = "12345";

      Notification notification =
          Notification.forComment(reviewId, receiverId, nickname, comment, reviewTitle);

      // when
      Notification saved = notificationRepository.save(notification);

      // then
      assertThat(saved.getId()).isNotNull();
      assertThat(saved.getContent()).isEqualTo("[buzz]님이 나의 리뷰에 댓글을 남겼습니다.\n12345");
      assertThat(saved.getReviewId()).isEqualTo(reviewId);
      assertThat(saved.getReceiverId()).isEqualTo(receiverId);
      assertThat(saved.isConfirmed()).isFalse();
      assertThat(saved.getCreatedAt()).isNotNull();
      assertThat(saved.getUpdatedAt()).isNotNull();
    }
  }

  @Nested
  @DisplayName("알림 조회")
  class FindNotificationTest {

    @BeforeEach
    void clearNotifications() {
      notificationRepository.deleteAll();
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("사용자의 모든 알림을 조회한다")
    void findAllByReceiverId() {
      // 이제 seeded 알림은 지워졌으니, 아래 n1,n2,n3 만 DB에 남습니다.
      UUID receiverId = seededUserId;
      String reviewTitle = "~책에 대한 리뷰";

      Notification n1 = Notification.forLike(seededReviewId, receiverId, "buzz", reviewTitle);
      Notification n2 = Notification.forComment(seededReviewId, receiverId, "buzz", "댓글", reviewTitle);
      Notification n3 = Notification.forLike(seededReviewId, null,      "buzz", reviewTitle);

      notificationRepository.saveAll(List.of(n1, n2, n3));
      em.flush();
      em.clear();

      List<Notification> result = notificationRepository.findAllByReceiverId(receiverId);

      assertThat(result)
          .hasSize(2)
          .allMatch(n -> n.getReceiverId().equals(receiverId));
    }
  }

  @Nested
  @DisplayName("알림 삭제")
  class DeleteNotificationTest {

    @BeforeEach
    void clearNotifications() {
      notificationRepository.deleteAll();
      em.flush();
      em.clear();
    }

    @Test
    @Transactional
    @DisplayName("1주일이 지난 확인된 알림은 삭제된다")
    void deleteOldConfirmedNotifications() {
      // Given
      UUID receiverId = seededUserId;
      String nickname = "buzz";
      String reviewTitle = "테스트 리뷰 제목";
      Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);

      Notification oldConfirmed = Notification.forLike(
          seededReviewId, receiverId, nickname, reviewTitle);
      oldConfirmed.markAsConfirmed();
      // DB 반영 전 updatedAt 조작
      ReflectionTestUtils.setField(oldConfirmed,
          "updatedAt", cutoff.minus(1, ChronoUnit.DAYS));

      Notification recentConfirmed = Notification.forLike(
          seededReviewId, receiverId, nickname, reviewTitle);
      recentConfirmed.markAsConfirmed();
      ReflectionTestUtils.setField(recentConfirmed,
          "updatedAt", Instant.now());

      Notification unconfirmed = Notification.forLike(
          seededReviewId, receiverId, nickname, reviewTitle);

      notificationRepository.saveAll(
          List.of(oldConfirmed, recentConfirmed, unconfirmed));
      notificationRepository.flush();

      // 실제 DB에도 업데이트 타임 반영
      em.createQuery(
              "UPDATE Notification n SET n.updatedAt = :updatedAt WHERE n.id = :id")
          .setParameter("updatedAt", cutoff.minus(1, ChronoUnit.DAYS))
          .setParameter("id", oldConfirmed.getId())
          .executeUpdate();

      em.clear();

      // When
      notificationRepository.deleteOldConfirmedNotifications(cutoff);

      // Then
      List<Notification> remaining = notificationRepository.findAll();
      List<UUID> remainingIds = remaining.stream()
          .map(Notification::getId).toList();
      assertThat(remainingIds).containsExactlyInAnyOrder(
          recentConfirmed.getId(), unconfirmed.getId());
    }
  }

  @Nested
  @DisplayName("알림 일괄 확인")
  class BulkMarkAsConfirmedTest {

    @Test
    @Transactional
    @DisplayName("모든 미확인 알림이 confirmed=true 로 업데이트된다")
    void bulkMarkAsConfirmed() {
      // Given
      UUID receiverId = seededUserId;
      Instant now = Instant.now();

      Notification n1 = Notification.forLike(
          seededReviewId, receiverId, "buzz", "타이틀");
      Notification n2 = Notification.forComment(
          seededReviewId, receiverId, "buzz", "댓글", "타이틀");

      notificationRepository.saveAll(List.of(n1, n2));
      em.flush();
      em.clear();

      // When
      notificationRepository.bulkMarkAsConfirmed(receiverId, now);
      em.flush();
      em.clear();

      // Then
      List<Notification> updated =
          notificationRepository.findAllByReceiverId(receiverId);
      assertThat(updated)
          .allMatch(n -> n.isConfirmed() && n.getUpdatedAt().equals(now));
    }
  }

  @Nested
  @DisplayName("페이징 커서 조회")
  class CursorPaginationTest {

    private UUID receiverId;
    private List<Notification> all;

    @BeforeEach
    void setUp() {
      // 항상 같은 seededUserId, seededReviewId 사용
      receiverId = seededUserId;
      all = IntStream.range(0, 5)
          .mapToObj(i -> {
            Notification n = Notification.forLike(
                seededReviewId, receiverId, "buzz", "타이틀");
            ReflectionTestUtils.setField(
                n, "createdAt", Instant.now().minus(i, ChronoUnit.MINUTES));
            return n;
          })
          .collect(Collectors.toList());

      notificationRepository.saveAll(all);
      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("DESC 커서 없을 때 페이지 크기만큼 조회")
    void findDescNoCursor() {
      Pageable page = PageRequest.of(0, 2,
          Sort.by("createdAt").descending());
      List<Notification> page1 =
          notificationRepository.findDescNoCursor(receiverId, page);

      assertThat(page1)
          .hasSize(2)
          .isSortedAccordingTo(
              (a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
    }

    @Test
    @DisplayName("DESC 커서 있을 때 이후 조회")
    void findDescWithCursor() {
      Pageable page = PageRequest.of(0, 2,
          Sort.by("createdAt").descending());
      List<Notification> page1 =
          notificationRepository.findDescNoCursor(receiverId, page);
      Instant cursor = page1.get(1).getCreatedAt();

      List<Notification> page2 =
          notificationRepository.findDescWithCursor(receiverId, cursor, page);
      assertThat(page2)
          .allMatch(n -> n.getCreatedAt().isBefore(cursor));
    }

    @Test
    @DisplayName("ASC 커서 없을 때 페이지 크기만큼 조회")
    void findAscNoCursor() {
      Pageable page = PageRequest.of(0, 2,
          Sort.by("createdAt").ascending());
      List<Notification> page1 =
          notificationRepository.findAscNoCursor(receiverId, page);

      assertThat(page1)
          .hasSize(2)
          .isSortedAccordingTo(
              Comparator.comparing(Notification::getCreatedAt));
    }

    @Test
    @DisplayName("ASC 커서 있을 때 이후 조회")
    void findAscWithCursor() {
      Pageable page = PageRequest.of(0, 2,
          Sort.by("createdAt").ascending());
      List<Notification> page1 =
          notificationRepository.findAscNoCursor(receiverId, page);
      Instant cursor = page1.get(1).getCreatedAt();

      List<Notification> page2 =
          notificationRepository.findAscWithCursor(receiverId, cursor, page);
      assertThat(page2)
          .allMatch(n -> n.getCreatedAt().isAfter(cursor));
    }
  }
}
