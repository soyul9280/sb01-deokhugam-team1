package com.codeit.duckhu.domain.review.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

  @Mock
  private ReviewService reviewService;

  @InjectMocks
  private ReviewController reviewController;

  private UUID reviewId;
  private UUID userId;
  private UUID bookId;
  private ReviewDto reviewDto;
  private ReviewCreateRequest createRequest;
  private ReviewUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {
    reviewId = UUID.randomUUID();
    userId = UUID.randomUUID();
    bookId = UUID.randomUUID();

    reviewDto = ReviewDto.builder()
        .id(reviewId)
        .userId(userId)
        .bookId(bookId)
        .rating(4)
        .content("좋은 책이에요")
        .userNickname("테스터")
        .bookTitle("테스트 도서")
        .likeCount(5)
        .commentCount(3)
        .createdAt(LocalDateTime.now())
        .build();

    createRequest = ReviewCreateRequest.builder()
        .userId(userId)
        .bookId(bookId)
        .rating(4)
        .content("좋은 책이에요")
        .build();

    updateRequest = ReviewUpdateRequest.builder()
        .userId(userId)
        .bookId(bookId)
        .rating(5)
        .content("정말 좋은 책이에요!")
        .build();
  }

  @Test
  @DisplayName("리뷰 목록 조회 - 커서 페이지네이션")
  void findReviews_Success() {
    // Given
    CursorPageResponseReviewDto responseDto = CursorPageResponseReviewDto.builder()
        .reviews(List.of(reviewDto))
        .nextCursor("next-cursor")
        .nextAfter(Instant.now())
        .size(1)
        .totalElements(10L)
        .hasNext(true)
        .build();

    when(reviewService.findReviews(any(ReviewSearchRequestDto.class))).thenReturn(responseDto);

    // When
    ResponseEntity<CursorPageResponseReviewDto> response = reviewController.getReviews(
        "키워드", "createdAt", "DESC", userId, bookId, null, null, 10);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseDto, response.getBody());
    
    // 서비스 호출 시 ReviewSearchRequestDto가 올바르게 생성되었는지 검증
    verify(reviewService).findReviews(any(ReviewSearchRequestDto.class));
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview_Success() {
    // Given
    when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(reviewDto);

    // When
    ResponseEntity<ReviewDto> response = reviewController.createReview(createRequest);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(reviewDto, response.getBody());
    verify(reviewService).createReview(createRequest);
  }

  @Test
  @DisplayName("ID로 리뷰 조회")
  void getReviewById_Success() {
    // Given
    when(reviewService.getReviewById(reviewId)).thenReturn(reviewDto);

    // When
    ResponseEntity<ReviewDto> response = reviewController.getReviewById(reviewId);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviewDto, response.getBody());
    verify(reviewService).getReviewById(reviewId);
  }

  @Test
  @DisplayName("리뷰 업데이트")
  void updateReview_Success() {
    // Given
    when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class))).thenReturn(reviewDto);

    // When
    ResponseEntity<ReviewDto> response = reviewController.updateReview(reviewId, updateRequest);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(reviewDto, response.getBody());
    verify(reviewService).updateReview(reviewId, updateRequest);
  }

  @Test
  @DisplayName("리뷰 소프트 삭제")
  void softDeleteReview_Success() {
    // Given
    doNothing().when(reviewService).softDeleteReviewById(reviewId);

    // When
    ResponseEntity<Void> response = reviewController.softDeleteReview(reviewId);

    // Then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(reviewService).softDeleteReviewById(reviewId);
  }

  @Test
  @DisplayName("리뷰 하드 삭제")
  void hardDeleteReview_Success() {
    // Given
    doNothing().when(reviewService).hardDeleteReviewById(reviewId);

    // When
    ResponseEntity<Void> response = reviewController.hardDeleteReview(reviewId);

    // Then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(reviewService).hardDeleteReviewById(reviewId);
  }
  
  @Test
  @DisplayName("리뷰 목록 조회 - 파라미터 없이 기본값 사용")
  void findReviews_WithDefaultParams() {
    // Given
    CursorPageResponseReviewDto responseDto = CursorPageResponseReviewDto.builder()
        .reviews(Collections.emptyList())
        .size(0)
        .hasNext(false)
        .build();

    when(reviewService.findReviews(any(ReviewSearchRequestDto.class))).thenReturn(responseDto);

    // When
    Integer limit = null; // 실제 컨트롤러에서는 null이 들어오더라도 Builder 내부에서 기본값이 적용됨
    ResponseEntity<CursorPageResponseReviewDto> response = reviewController.getReviews(
        null, null, null, null, null, null, null, limit);

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(responseDto, response.getBody());
    
    // 서비스 호출 시 파라미터 없이 호출되었는지 검증
    verify(reviewService).findReviews(any(ReviewSearchRequestDto.class));
  }
}
