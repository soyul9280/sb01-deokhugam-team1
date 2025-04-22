package com.codeit.duckhu.domain.review.service.impl;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
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
import com.codeit.duckhu.domain.user.exception.NotFoundUserException;
import com.codeit.duckhu.domain.user.repository.UserRepository;
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
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.USER_NOT_FOUND));
    
    // 도서 찾기
    Book book = bookRepository.findById(request.getBookId())
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.BOOK_NOT_FOUND));
    
    // 동일한 도서에 대한 리뷰가 이미 존재하는지 확인
    reviewRepository.findByUserIdAndBookId(request.getUserId(), request.getBookId())
        .ifPresent(existingReview -> {
            throw new ReviewCustomException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        });
    
    // 매퍼를 사용하여 엔티티 생성
    Review review = reviewMapper.toEntity(request, user, book);
    
    // 저장 및 DTO 반환
    Review savedReview = reviewRepository.save(review);

    // jw
    recalculateBookStats(book);

    return reviewMapper.toDto(savedReview);
  }


  @Override
  public ReviewDto getReviewById(UUID id) {
    log.info("리뷰 조회, ID: {}", id);

    // 리뷰 조회
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (review.isDeleted()) {
      throw new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND);
    }

    // DTO로 변환하여 반환
    return reviewMapper.toDto(review);
  }

  @Transactional
  @Override
  public void hardDeleteReviewById(UUID id) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    reviewRepository.delete(review);

    // jw
    recalculateBookStats(review.getBook());
  }

  @Transactional
  @Override
  public void softDeleteReviewById(UUID id) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    review.softDelete();

    // jw
    recalculateBookStats(review.getBook());
  }

  @Transactional
  @Override
  public ReviewDto updateReview(UUID id, ReviewUpdateRequest request) {
    Review review = reviewRepository.findById(id)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    // 사용자 찾기
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.USER_NOT_FOUND));

    if (review.isDeleted()) {
      throw new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND);
    }

    if(!user.getId().equals(review.getUser().getId()))  {
      throw new ReviewCustomException(ReviewErrorCode.USER_NOT_OWNER);
    }

    review.updateContent(request.getContent());
    review.updateRating(request.getRating());

    Review updatedReview = reviewRepository.save(review);
    log.info("리뷰 업데이트 성공, ID: {}", updatedReview.getId());

    // jw
    recalculateBookStats(updatedReview.getBook());

    return reviewMapper.toDto(updatedReview);
  }

  @Transactional
  @Override
  public ReviewLikeDto likeReview(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    if (review.isDeleted()) {
      throw new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND);
    }

    // 사용자 찾기
    userRepository.existsById(userId);

    boolean likedBefore = review.liked(userId);

    if (likedBefore) {
      review.decreaseLikeCount(userId);
    } else {
      review.increaseLikeCount(userId);
    }

    boolean likedAfter = review.liked(userId);
    return ReviewLikeDto.builder()
        .reviewId(review.getId())
        .userId(userId)
        .liked(likedAfter)
        .build();

  }
  public Review findByIdEntityReturn(UUID reviewId){
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
  }

  @Transactional
  @Override
  public ReviewLikeDto likeReview(UUID reviewId, UUID userId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.REVIEW_NOT_FOUND));

    userRepository.findById(userId)
        .orElseThrow(() -> new ReviewCustomException(ReviewErrorCode.USER_NOT_FOUND));

    boolean likedBefore = review.liked(userId);

    if (likedBefore) {
      review.decreaseLikeCount(userId);
    } else {
      review.increaseLikeCount(userId);
    }


    boolean likedAfter = review.liked(userId);
    return ReviewLikeDto.builder()
        .reviewId(review.getId())
        .userId(userId)
        .liked(likedAfter)
        .build();
  }

  // 도서에 관련된 집계 필드 업데이트 - jw
  private void recalculateBookStats(Book book) {
    // 도서에 작성된 리뷰 개수 조회 - jw
    int reviewCount = reviewRepository.countByBookId(book.getId());
    // 도서에 대한 평균 평점을 계산 - jw
    double rating = reviewRepository.calculateAverageRatingByBookId(book.getId());
    // 조회된 리뷰 개수와 평균 평점을 Book 엔티티에 반영 - jw
    book.updateReviewStatus(reviewCount, rating);
  }
}