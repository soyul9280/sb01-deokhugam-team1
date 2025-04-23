package com.codeit.duckhu.domain.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReviewService reviewService;

  @Autowired
  private ObjectMapper objectMapper;
  
  @Test
  @DisplayName("리뷰 목록 조회 - 커서 페이지네이션")
  void findReviews_Success() throws Exception {
    // Given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    Instant now = Instant.now();
    String cursor = "next";
    int limit = 10;
    long total = 42L;
    boolean hasNext = true;

    ReviewDto reviewDto = ReviewDto.builder()
        .id(reviewId)
        .userId(userId)
        .bookId(bookId)
        .rating(5)
        .content("테스트 컨텐츠")
        .build();
    
    List<ReviewDto> reviews = List.of(reviewDto);

    CursorPageResponseReviewDto responseDto = CursorPageResponseReviewDto.builder()
        .reviews(reviews)
        .nextCursor(cursor)
        .nextAfter(now)
        .size(limit)
        .totalElements(total)
        .hasNext(hasNext)
        .build();

    given(reviewService.findReviews(any(ReviewSearchRequestDto.class)))
        .willReturn(responseDto);

    // When & Then
    mockMvc.perform(get("/api/reviews")
            .param("keyword", "재밌어요")
            .param("orderBy", "createdAt")
            .param("direction", "DESC")
            .param("userId", userId.toString())
            .param("bookId", bookId.toString())
            .param("limit", String.valueOf(limit))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.reviews.length()").value(reviews.size()))
        .andExpect(jsonPath("$.reviews[0].id").value(reviewId.toString()))
        .andExpect(jsonPath("$.reviews[0].rating").value(reviewDto.getRating()))
        .andExpect(jsonPath("$.reviews[0].content").value(reviewDto.getContent()))
        .andExpect(jsonPath("$.nextCursor").value(cursor))
        .andExpect(jsonPath("$.nextAfter").value(now.toString()))
        .andExpect(jsonPath("$.size").value(limit))
        .andExpect(jsonPath("$.totalElements").value(total))
        .andExpect(jsonPath("$.hasNext").value(hasNext));
  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview_Success() throws Exception {
    // Given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID bookId = UUID.randomUUID();
    
    ReviewCreateRequest request = ReviewCreateRequest.builder()
        .userId(userId)
        .bookId(bookId)
        .rating(5)
        .content("재밌어요!")
        .build();

    ReviewDto responseDto = ReviewDto.builder()
        .id(reviewId)
        .userId(userId)
        .bookId(bookId)
        .rating(5)
        .content("재밌어요!")
        .build();

    given(reviewService.createReview(any(ReviewCreateRequest.class)))
        .willReturn(responseDto);

    // When & Then
    mockMvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(reviewId.toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.bookId").value(bookId.toString()))
        .andExpect(jsonPath("$.rating").value(5))
        .andExpect(jsonPath("$.content").value("재밌어요!"));
  }
}
