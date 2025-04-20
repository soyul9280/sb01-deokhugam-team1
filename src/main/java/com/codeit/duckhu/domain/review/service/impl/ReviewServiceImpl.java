package com.codeit.duckhu.domain.review.service.impl;

import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.global.exception.CustomException;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.exception.ReviewCustomException;
import com.codeit.duckhu.domain.review.exception.ReviewErrorCode;
import com.codeit.duckhu.domain.review.mapper.ReviewMapper;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.ReviewService;
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
@Transactional(readOnly = true)
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
        // TODO: 사용자, 도서 추가
        .build();

      // 리뷰 저장
      Review savedReview = reviewRepository.save(review);
      log.info("저장 성공, ID: {}", savedReview.getId());
      
      // DTO로 변환하여 반환
      return reviewMapper.toDto(savedReview);
  }

  @Override
  public ReviewDto getReviewById(UUID id) {
    log.info("리뷰 조회, ID: {}", id);

    // 리뷰 조회
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    // DTO로 변환하여 반환
    return reviewMapper.toDto(review);
  }

  @Transactional
  @Override
  public void deleteReviewById(UUID id) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    reviewRepository.delete(review);
  }

  @Transactional
  @Override
  public ReviewDto updateReview(UUID id, ReviewUpdateRequest reviewUpdateRequest) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    // TODO:작성자 확인 - 작성자와 현재 사용자가 같은지 확인하는 로직 추가 필요 (User 통합시 추가)

    review.updateContent(reviewUpdateRequest.getContent());
    review.updateRating(reviewUpdateRequest.getRating());

    Review updatedReview = reviewRepository.save(review);
    log.info("리뷰 업데이트 성공, ID: {}", updatedReview.getId());

    return reviewMapper.toDto(updatedReview);
  }
}