package com.codeit.duckhu.domain.review.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.global.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
public class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ReviewService reviewService;

  @Autowired
  private ObjectMapper objectMapper;


  @Nested
  @DisplayName("리뷰 생성 테스트")
  class createReview {

    @Test
    @DisplayName("POST /api/reviews-성공")
    void createReview_Success() throws Exception {
      // Given
      ReviewCreateRequest request = ReviewCreateRequest.builder()
          .userId(UUID.randomUUID())
          .bookId(UUID.randomUUID())
          .rating(5)
          .content("재밌어요 !")
          .build();

      ReviewDto review = ReviewDto.builder()
          .id(UUID.randomUUID())
          .userId(request.getUserId())
          .bookId(request.getBookId())
          .rating(request.getRating())
          .content(request.getContent())
          .createdAt(null)
          .updatedAt(null)
          .build();

      given(reviewService.createReview(any(ReviewCreateRequest.class)))
          .willReturn(review);

      mockMvc.perform(post("/api/reviews")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(review.getId().toString()))
          .andExpect(jsonPath("$.userId").value(review.getUserId().toString()))
          .andExpect(jsonPath("$.bookId").value(review.getBookId().toString()))
          .andExpect(jsonPath("$.rating").value(review.getRating()))
          .andExpect(jsonPath("$.content").value(review.getContent()));
    }

    @Test
    @DisplayName("POST /api/reviews-파라미터 누락")
    void createReview_Fail() throws Exception {
      // Given
      ReviewCreateRequest request = ReviewCreateRequest.builder()
          .userId(null)
          .bookId(null)
          .rating(null)
          .content("재밌어요 !")
          .build();

      // When & Then
      mockMvc.perform(post("/api/reviews")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("리뷰 수정 테스트")
  class updateReview {
    @Test
    @DisplayName("PATCH /api/reviews-성공")
    void updateReview_Success() throws Exception{
      UUID reviewId = UUID.randomUUID();

      ReviewUpdateRequest request = ReviewUpdateRequest.builder()
          .userId(UUID.randomUUID())
          .bookId(UUID.randomUUID())
          .rating(4)
          .content("볼만해요 !")
          .build();

      ReviewDto review = ReviewDto.builder()
          .id(reviewId)
          .userId(UUID.randomUUID())
          .bookId(UUID.randomUUID())
          .rating(request.getRating())
          .content(request.getContent())
          .build();

      given(reviewService.updateReview(any(UUID.class), any(ReviewUpdateRequest.class)))
          .willReturn(review);

      mockMvc.perform(patch("/api/reviews/{reviewId}", reviewId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.rating").value(request.getRating()))
          .andExpect(jsonPath("$.content").value(request.getContent()));
    }
  }

  @Nested
  @DisplayName("리뷰 삭제 테스트")
  class deleteReview {

    @Test
    @DisplayName("DELETE /api/reviews/{reviewId} - 논리 삭제 성공")
    void softDeleteReview_Success() throws Exception {
      UUID reviewId = UUID.randomUUID();

      mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/reviews/{reviewId}/hard - 물리 삭제 성공")
    void hardDeleteReview_Success() throws Exception {
      UUID reviewId = UUID.randomUUID();

      mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId))
          .andExpect(status().isNoContent());
    }
  }
}
