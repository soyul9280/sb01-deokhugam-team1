package com.codeit.duckhu.domain.review.service;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.entity.Review;
import java.util.UUID;

/** 리뷰 서비스 인터페이스 */
public interface ReviewService {

  ReviewDto createReview(ReviewCreateRequest request);

  ReviewDto getReviewById(UUID userId, UUID reviewId);

  void hardDeleteReviewById(UUID userId, UUID reviewId);

  ReviewDto updateReview(UUID userId, UUID reviewId, ReviewUpdateRequest request);

  ReviewLikeDto likeReview(UUID reviewId, UUID userId);

  CursorPageResponseReviewDto findReviews(ReviewSearchRequestDto requestDto);

  void softDeleteReviewById(UUID userId, UUID reviewId);

  Review findByIdEntityReturn(UUID reviewId);
}
