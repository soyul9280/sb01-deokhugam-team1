package com.codeit.duckhu.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

  private MockMvc mockMvc;
  
  private ObjectMapper objectMapper;

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
  private ReviewLikeDto reviewLikeDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    objectMapper = new ObjectMapper();
    
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

    reviewLikeDto = ReviewLikeDto.builder()
               .reviewId(reviewId)
               .userId(userId)
               .liked(true)
               .build();
  }

  @Test
  @DisplayName("리뷰 목록 조회 - 커서 페이지네이션")
  void findReviews_Success() throws Exception {
    // Given
    CursorPageResponseReviewDto responseDto = CursorPageResponseReviewDto.builder()
        .content(List.of(reviewDto))
        .nextCursor("next-cursor")
        .nextAfter(Instant.now())
        .size(1)
        .totalElements(10L)
        .hasNext(true)
        .build();

    when(reviewService.findReviews(any(ReviewSearchRequestDto.class), any(UUID.class)))
        .thenReturn(responseDto);

    // When & Then
    mockMvc.perform(get("/api/reviews")
            .param("keyword", "키워드")
            .param("orderBy", "createdAt")
            .param("direction", "DESC")
            .param("userId", userId.toString())
            .param("bookId", bookId.toString())
            .param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content[0].content").value("좋은 책이에요"))
        .andExpect(jsonPath("$.hasNext").value(true));
    
    // 서비스 호출 검증
    verify(reviewService).findReviews(any(ReviewSearchRequestDto.class), any(UUID.class));
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview_Success() throws Exception {
    // Given
    when(reviewService.createReview(any(ReviewCreateRequest.class))).thenReturn(reviewDto);

    // When & Then
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("좋은 책이에요"));
    
    // 서비스 호출 검증
    verify(reviewService).createReview(any(ReviewCreateRequest.class));
  }

  @Test
  @DisplayName("리뷰 좋아요")
  void createReviewLike_Success() throws Exception {
    // Given
    when(reviewService.likeReview(reviewId, userId)).thenReturn(reviewLikeDto);

    // When & Then
    mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
            .header("X-USER-ID", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reviewId").value(reviewId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.liked").value(true));
    
    // 서비스 호출 검증
    verify(reviewService).likeReview(reviewId, userId);
  }

  @Test
  @DisplayName("ID로 리뷰 조회")
  void getReviewById_Success() throws Exception {
    // Given
    when(reviewService.getReviewById(userId, reviewId)).thenReturn(reviewDto);

    // When & Then
    mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.content").value("좋은 책이에요"));
    
    // 서비스 호출 검증
    verify(reviewService).getReviewById(userId, reviewId);
  }

  @Test
  @DisplayName("리뷰 업데이트")
  void updateReview_Success() throws Exception {
    // Given
    when(reviewService.updateReview(eq(userId), eq(reviewId), any(ReviewUpdateRequest.class))).thenReturn(reviewDto);

    // When & Then
    mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
            .param("userId", userId.toString())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(reviewId.toString()));
    
    // 서비스 호출 검증
    verify(reviewService).updateReview(eq(userId), eq(reviewId), any(ReviewUpdateRequest.class));
  }

  @Test
  @DisplayName("리뷰 소프트 삭제")
  void softDeleteReview_Success() throws Exception {
    // Given
    doNothing().when(reviewService).softDeleteReviewById(userId, reviewId);

    // When & Then
    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
            .param("userId", userId.toString()))
        .andExpect(status().isNoContent());
    
    // 서비스 호출 검증
    verify(reviewService).softDeleteReviewById(userId, reviewId);
  }

  @Test
  @DisplayName("리뷰 하드 삭제")
  void hardDeleteReview_Success() throws Exception {
    // Given
    doNothing().when(reviewService).hardDeleteReviewById(userId, reviewId);

    // When & Then
    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
            .param("userId", userId.toString()))
        .andExpect(status().isNoContent());
    
    // 서비스 호출 검증
    verify(reviewService).hardDeleteReviewById(userId, reviewId);
  }
}
