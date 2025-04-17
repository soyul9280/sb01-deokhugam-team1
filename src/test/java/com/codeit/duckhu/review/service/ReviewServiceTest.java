package com.codeit.duckhu.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.codeit.duckhu.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.review.dto.ReviewDto;
import com.codeit.duckhu.review.entity.Review;
import com.codeit.duckhu.review.mapper.ReviewMapper;
import com.codeit.duckhu.review.repository.ReviewRepository;
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

  @Mock
  private ReviewRepository reviewRepository;
  
  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewServiceImpl reviewService;
  
  /**
   * 서비스 계층 설계
   * 
   * 1. ReviewService 인터페이스 생성
   *    - createReview: 리뷰 생성
   *    - getReview: ID로 리뷰 조회
   *    - updateReview: 리뷰 업데이트
   *    - deleteReview: 리뷰 삭제
   *    - likeReview: 리뷰 좋아요
   *    - getAll: 목록 조회(커서 페이지네이션)
   *
   * 2. DTO 클래스 설계
   *    - ReviewDto: 응답 데이터
   *    - ReviewCreateRequest: 생성 요청 데이터
   *    - ReviewUpdateRequest: 업데이트 요청 데이터
   *    - ReviewLikeDto: 리뷰 좋아요 데이터
   * 
   * 3. 서비스 구현체 (ReviewServiceImpl) 생성
   *    - 리포지토리를 통한 CRUD 구현
   */
  
  @Test
  @DisplayName("리뷰 생성 테스트")
  void 리뷰_생성() {
      // Given
      ReviewCreateRequest request = ReviewCreateRequest.builder()
         .rating(3)
         .content("볼만해요")
         .build();
      
      Review review = Review.builder()
          .rating(request.getRating())
          .content(request.getContent())
          .build();

      ReviewDto expectedDto = ReviewDto.builder()
          .content("볼만해요")
          .rating(3)
          .build();
      
      when(reviewRepository.save(any(Review.class))).thenReturn(review);
      when(reviewMapper.toDto(any(Review.class))).thenReturn(expectedDto);

      // When
      ReviewDto result = reviewService.createReview(request);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getRating()).isEqualTo(3);
      assertThat(result.getContent()).isEqualTo("볼만해요");
  }
}
