package com.codeit.duckhu.review.service;

import com.codeit.duckhu.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.review.dto.ReviewDto;
import com.codeit.duckhu.review.entity.Review;
import com.codeit.duckhu.review.mapper.ReviewMapper;
import com.codeit.duckhu.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  @Override
  public ReviewDto createReview(ReviewCreateRequest request) {
    Review review = Review.builder()
        .content(request.getContent())
        .rating(request.getRating())
        .likeCount(0)
        .commentCount(0)
        .likeByMe(false)
        .build();

    Review saved = reviewRepository.save(review);
    return reviewMapper.toDto(saved);
  }
} 