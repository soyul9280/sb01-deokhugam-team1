package com.codeit.duckhu.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewControllerTest {

  private MockMvc mockMvc;
  
  private ObjectMapper objectMapper;

  @Mock
  private ReviewService reviewService;
  
  @Mock
  private User mockUser;
  
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
    // 실제 컨트롤러 생성
    reviewController = new ReviewController(reviewService);
    
    // MockMvc 설정
    mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
        .addFilter(new CharacterEncodingFilter("UTF-8", true))
        .build();
    
    objectMapper = new ObjectMapper();
    
    reviewId = UUID.randomUUID();
    userId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    
    // Mock User 설정
    when(mockUser.getId()).thenReturn(userId);

    reviewDto = ReviewDto.builder()
        .id(reviewId)
        .userId(userId)
        .bookId(bookId)
        .rating(4)
        .content("좋은 책이에요")
        .userNickname("테스터")
        .bookTitle("테스트 도서")
        .bookThumbnailUrl("http://example.com/test.jpg")
        .likeCount(5)
        .commentCount(3)
        .likedByMe(false)
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

  // 인증된 사용자 정보를 요청에 추가하는 헬퍼 메소드
  private RequestPostProcessor withAuthenticatedUser() {
    return request -> {
      request.setAttribute("authenticatedUser", mockUser);
      return request;
    };
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
        .hasNext(true)
        .build();

    // 서비스 메소드 모킹 - 컨트롤러 메소드에서 필요한 경우
    when(reviewService.findReviews(any(), any())).thenReturn(responseDto);

    // When & Then - 필요한 응답 코드만 검증
    mockMvc.perform(get("/api/reviews")
            .param("keyword", "키워드")
            .param("orderBy", "createdAt")
            .param("direction", "DESC")
            .param("userId", userId.toString())
            .param("bookId", bookId.toString())
            .param("limit", "10")
            .header("X-USER-ID", userId.toString())
            .with(withAuthenticatedUser()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size").exists());
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
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("ID로 리뷰 조회")
  void getReviewById_Success() throws Exception {
    // 서비스 메소드 모킹
    when(reviewService.getReviewById(eq(userId), eq(reviewId))).thenReturn(reviewDto);

    // 요청에 인증 사용자 정보 추가
    mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
            .header("X-USER-ID", userId.toString())
            .with(withAuthenticatedUser())) // 이 부분이 핵심입니다
        .andDo(print())
        .andExpect(status().isOk());
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
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("리뷰 소프트 삭제")
  void softDeleteReview_Success() throws Exception {
    // Given
    doNothing().when(reviewService).softDeleteReviewById(eq(userId), eq(reviewId));

    // When & Then
    mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
            .param("userId", userId.toString()))
        .andDo(print())
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("리뷰 하드 삭제")
  void hardDeleteReview_Success() throws Exception {
    // Given
    doNothing().when(reviewService).hardDeleteReviewById(eq(userId), eq(reviewId));

    // When & Then
    mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
            .param("userId", userId.toString()))
        .andDo(print())
        .andExpect(status().isNoContent());
  }
}
