package com.codeit.duckhu.domain.review.service.impl;

import com.codeit.duckhu.domain.comment.repository.CommentRepository;
import com.codeit.duckhu.domain.notification.service.NotificationService;
import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.LikedUserIdRepository;
import com.codeit.duckhu.domain.review.repository.PopularReviewRepository;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인기 리뷰 점수 계산 및 랭킹 업데이트를 위한 배치 서비스 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PopularReviewBatchService {

  private final ReviewRepository reviewRepository;
  private final PopularReviewRepository popularReviewRepository;
  private final LikedUserIdRepository likedUserIdRepository;
  private final CommentRepository commentRepository;
  private final NotificationService notificationService;

  /**
   * 매일 새벽 12시에 실행
   */
  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void calculatePopularReviews() {
    log.info("인기 리뷰 점수 계산 및 랭킹 업데이트");
    Instant now = Instant.now();

    List<Review> allActiveReviews = reviewRepository.findAllByIsDeletedFalse();

    for (PeriodType period : PeriodType.values()) {
      // ALL_TIME은 startTime을 null로 설정하여 전체 기간을 대상으로 합니다.
      Instant startTime = period.toStartInstant(now);
      log.info("{} 기간 인기 리뷰 계산 시작 (시작 시간: {})", period, startTime);

      // 삭제되지 않은 모든 리뷰 조회
      List<PopularReviewData> scoredReviews = new ArrayList<>();

      for (Review review : allActiveReviews) {
        int likesInPeriod;
        int commentsInPeriod;

        // 기간별 좋아요/댓글 수 계산
        likesInPeriod = likedUserIdRepository.countByReviewAndCreatedAtBetween(review, startTime, now);
        commentsInPeriod = commentRepository.countByReviewAndIsDeletedFalseAndCreatedAtBetween(review, startTime, now);


        // 좋아요 또는 댓글이 있는 경우에만 점수 계산
        if (likesInPeriod > 0 || commentsInPeriod > 0) {
          double score = (likesInPeriod * 0.3) + (commentsInPeriod * 0.7);
          scoredReviews.add(new PopularReviewData(review, score, likesInPeriod, commentsInPeriod));
        }
      }

      // 점수 내림차순 정렬
      scoredReviews.sort(Comparator.comparingDouble(PopularReviewData::getScore).reversed());

      // 기존 데이터 삭제
      popularReviewRepository.deleteByPeriod(period);
      log.debug("{} 기간 기존 인기 리뷰 데이터 삭제 완료", period);

      int rank = 1;
      List<PopularReview> popularReviewsToSave = new ArrayList<>();
      for (PopularReviewData data : scoredReviews) {

        PopularReview popularReview = PopularReview.builder()
            .review(data.getReview())
            .period(period)
            .score(data.getScore())
            .rank(rank)
            .likeCount(data.getLikesInPeriod())
            .commentCount(data.getCommentsInPeriod())
            .reviewRating((double) data.getReview().getRating())
            .build();
        popularReviewsToSave.add(popularReview);

        // 10위 안에 드는 경우 알림 생성
        if (rank <= 10) {
          UUID reviewId   = data.getReview().getId();
          UUID receiverId = data.getReview().getUser().getId();
          notificationService.createNotifyByPopularReview(
              reviewId, receiverId, period, rank
          );
        }
        rank++;
      }

      if (!popularReviewsToSave.isEmpty()) {
        popularReviewRepository.saveAll(popularReviewsToSave);
      } else {
        log.info("{} 기간 인기 리뷰 없음", period);
      }
    }
    log.info("인기 리뷰 점수 계산 및 랭킹 업데이트 완료");
  }

  @RequiredArgsConstructor
  @Getter
  private static class PopularReviewData {
    private final Review review;
    private final double score;
    private final int likesInPeriod;
    private final int commentsInPeriod;
  }
} 