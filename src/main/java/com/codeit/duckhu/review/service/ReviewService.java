package com.codeit.duckhu.review.service;

import com.codeit.duckhu.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.review.dto.ReviewDto;

public interface ReviewService {
  
  ReviewDto createReview(ReviewCreateRequest createRequest);
}
