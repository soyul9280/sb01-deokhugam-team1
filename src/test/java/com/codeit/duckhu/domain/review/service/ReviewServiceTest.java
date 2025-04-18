package com.codeit.duckhu.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.mapper.ReviewMapper;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 리뷰 서비스 테스트 클래스
 * TDD 방식으로 구현 예정
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  /**
   * 서비스 계층 설계
   *
   * 1. ReviewService
   * - createReview: 리뷰 생성
   * - getReview: ID로 리뷰 조회
   * - updateReview: 리뷰 업데이트
   * - deleteReview: 리뷰 삭제
   * - likeReview: 리뷰 좋아요
   * - getAll: 목록 조회(커서 페이지네이션)
   *
   * 2. DTO 클래스 설계
   * - ReviewDto: 응답 데이터
   * - ReviewCreateRequest: 생성 요청 데이터
   * - ReviewUpdateRequest: 업데이트
   * 요청 데이터 - ReviewLikeDto: 리뷰 좋아요 데이터
   *
   * 3. 서비스 구현체 (ReviewServiceImpl) 생성 - 리포지토리를 통한 CRUD 구현
   */

  @Mock
  private ReviewRepository reviewRepository;
  
  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  private Review testReview;
  private ReviewDto testReviewDto;
  private ReviewCreateRequest testCreateRequest;
  private UUID testReviewId;

  @BeforeEach
  void setUp() {
    // 테스트용 ID 생성
    testReviewId = UUID.randomUUID();
    
    // 테스트용 리뷰 엔티티 생성
    testReview = Review.builder()
        .content("볼만해요")
        .rating(3)
        .likeCount(0)
        .commentCount(0)
        .build();

    // 테스트용 DTO 생성
    testReviewDto = ReviewDto.builder()
        .content("볼만해요")
        .rating(3)
        .commentCount(0)
        .likeCount(0)
        .likedByMe(false)
        .build();
        
    // 테스트용 Create 요청 생성
    testCreateRequest = ReviewCreateRequest.builder()
        .content("볼만해요")
        .rating(3)
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 성공")
  void createReview_shouldCreateReview() {
    // Given
    when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
    when(reviewMapper.toDto(any(Review.class))).thenReturn(testReviewDto);

    // When
    ReviewDto result = reviewService.createReview(testCreateRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRating()).isEqualTo(testCreateRequest.getRating());
    assertThat(result.getContent()).isEqualTo(testCreateRequest.getContent());
    assertThat(result.getLikeCount()).isEqualTo(0);
    assertThat(result.getCommentCount()).isEqualTo(0);
    verify(reviewRepository).save(any(Review.class));
    verify(reviewMapper).toDto(any(Review.class));
  }

  @Test
  @DisplayName("ID로 리뷰 조회 테스트")
  void getReviewById_shouldReturnReview() {
    // Given : 저장된 리뷰를 찾았다고 가정, 엔티티를 Dto로 변환
    when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(testReview));
    when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);
    
    // When : id로 리뷰 찾기
    ReviewDto result = reviewService.getReviewById(testReviewId);
    
    // Then : null이 아니여야 하고, content, rating 검증
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo("볼만해요");
    assertThat(result.getRating()).isEqualTo(3);
  }

  @Test
  @DisplayName("ID로 리뷰 삭제 테스트")
  void deleteReviewById_shouldReturnSuccess() {
    // Given
    when(reviewRepository.findById(any(UUID.class))).thenReturn(Optional.of(testReview));
    willDoNothing().given(reviewRepository).delete(any(Review.class));

    // When
    assertDoesNotThrow(() -> reviewService.deleteReviewById(testReviewId));

    // Then
    verify(reviewRepository).deleteById(testReviewId);
    verify(reviewRepository, atLeastOnce()).deleteById(testReviewId);
  }
}

