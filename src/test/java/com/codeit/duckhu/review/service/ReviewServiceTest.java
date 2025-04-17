package com.codeit.duckhu.review.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.review.entity.Review;
import com.codeit.duckhu.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 리뷰 서비스 테스트 클래스
 * TDD 방식으로 구현 예정
 */
@SpringBootTest
class ReviewServiceTest {

  @Autowired
  private ReviewRepository reviewRepository;

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
       ReviewCreateDto createDto = ReviewCreateDto.builder()
          .rating(rating)
          .content(content)
          .build();

      // When
      ReviewDto result = reviewService.createReview(createDto);

      // Then
      assertThat(result).isNotNull();
      assertThat(result.getRating()).isEqualTo(rating);
      assertThat(result.getContent()).isEqualTo(content);
      assertThat(result.getLikeCount()).isEqualTo(0);
      assertThat(result.getCommentCount()).isEqualTo(0);
      assertThat(result.getLikeByMe()).isFalse();

  }
}
