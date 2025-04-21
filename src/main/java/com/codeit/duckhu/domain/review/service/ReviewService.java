package com.codeit.duckhu.domain.review.service;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import java.util.UUID;

/**
 * 리뷰 서비스 인터페이스
 */
public interface ReviewService {
  
  ReviewDto createReview(ReviewCreateRequest request);
  
  ReviewDto getReviewById(UUID id);
  
  void deleteReviewById(UUID id);

  ReviewDto updateReview(UUID id, ReviewUpdateRequest request);
}
