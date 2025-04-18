package com.codeit.duckhu.review.service;

import com.codeit.duckhu.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.review.dto.ReviewDto;
import java.util.UUID;

public interface ReviewService {
  
  ReviewDto createReview(ReviewCreateRequest createRequest);

  ReviewDto getReviewById(UUID testReviewId);
}
