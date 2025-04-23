package com.codeit.duckhu.domain.review.controller;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
public class ReviewController {

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

    // When

    // Then

  }

}
