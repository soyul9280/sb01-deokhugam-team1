package com.codeit.duckhu.review.service.impl;

import com.codeit.duckhu.global.exception.CustomException;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.codeit.duckhu.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.review.dto.ReviewDto;
import com.codeit.duckhu.review.entity.Review;
import com.codeit.duckhu.review.exception.ReviewCustomException;
import com.codeit.duckhu.review.exception.ReviewErrorCode;
import com.codeit.duckhu.review.mapper.ReviewMapper;
import com.codeit.duckhu.review.repository.ReviewRepository;
import com.codeit.duckhu.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 리뷰 서비스 구현체
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

  @Override
  @Transactional
  public ReviewDto createReview(@Valid ReviewCreateRequest request) {
    log.info("새로운 리뷰 생성, rating: {}", request.getRating());

    Review review = Review.builder()
        .content(request.getContent())
        .rating(request.getRating())
        .likeCount(0)
        .commentCount(0)
        .likeByMe(false)
        // TODO: 사용자, 도서 추가
        .build();

    try {
      // 리뷰 저장
      Review savedReview = reviewRepository.save(review);
      log.info("저장 성공, ID: {}", savedReview.getId());
      
      // DTO로 변환하여 반환
      return reviewMapper.toDto(savedReview);
    } catch (Exception e) {
      log.debug("리뷰 생성 실패 : {}", e.getMessage(), e);
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ReviewDto getReviewById(UUID testReviewId) {
    log.info("리뷰 조회, ID: {}", testReviewId);

    // 리뷰 조회
    Review review = reviewRepository.findById(testReviewId)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    // DTO로 변환하여 반환
    return reviewMapper.toDto(review);
  }
}