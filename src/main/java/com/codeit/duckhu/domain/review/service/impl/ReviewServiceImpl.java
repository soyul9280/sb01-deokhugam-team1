package com.codeit.duckhu.domain.review.service.impl;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.exception.ReviewCustomException;
import com.codeit.duckhu.domain.review.exception.ReviewErrorCode;
import com.codeit.duckhu.domain.review.mapper.ReviewMapper;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
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
  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ReviewDto createReview(ReviewCreateRequest request) {
    log.info("새로운 리뷰 생성, rating: {}", request.getRating());

    // 사용자 찾기
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    
    // 도서 찾기
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));
    
    // 동일한 도서에 대한 리뷰가 이미 존재하는지 확인
    reviewRepository.findByUserIdAndBookId(request.getUserId(), request.getBookId())
        .ifPresent(existingReview -> {
            throw new ReviewCustomException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        });
    
    // 매퍼를 사용하여 엔티티 생성
    Review review = reviewMapper.toEntity(request, user, book);
    
    // 저장 및 DTO 반환
    Review savedReview = reviewRepository.save(review);
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