package com.codeit.duckhu.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.global.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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

    // When

    // Then
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

}
