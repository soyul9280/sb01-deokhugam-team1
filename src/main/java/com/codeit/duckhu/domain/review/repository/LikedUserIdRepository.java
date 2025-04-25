package com.codeit.duckhu.domain.review.repository;

import com.codeit.duckhu.domain.review.entity.LikedUserId;
import com.codeit.duckhu.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface LikedUserIdRepository extends JpaRepository<LikedUserId, UUID> {

    int countByReviewAndCreatedAtBetween(Review review, Instant startTime, Instant endTime);

    int countByReview(Review review);
} 