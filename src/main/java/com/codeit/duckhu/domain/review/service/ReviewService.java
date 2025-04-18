package com.codeit.duckhu.domain.review.service;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import java.util.UUID;

public interface ReviewService {
  
  ReviewDto createReview(ReviewCreateRequest createRequest);

  ReviewDto getReviewById(UUID testReviewId);
}
