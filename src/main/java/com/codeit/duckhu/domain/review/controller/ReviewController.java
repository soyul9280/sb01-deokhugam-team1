package com.codeit.duckhu.domain.review.controller;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewCreateRequest request) {
    ReviewDto review = reviewService.createReview(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(review);
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> updateReview(
      @PathVariable("reviewId") UUID reviewId,
      @Valid @RequestBody ReviewUpdateRequest request) {
    ReviewDto review = reviewService.updateReview(reviewId, request);
    return ResponseEntity.ok(review);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> softDeleteReview(@PathVariable UUID reviewId) {
    reviewService.softDeleteReviewById(reviewId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDeleteReview(@PathVariable("reviewId") UUID reviewId) {
    reviewService.hardDeleteReviewById(reviewId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> getReviewById(@PathVariable("reviewId") UUID reviewId) {
    ReviewDto review = reviewService.getReviewById(reviewId);
    return ResponseEntity.ok(review);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseReviewDto> getReviews(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "orderBy", required = false) String orderBy,
      @RequestParam(name = "direction", required = false) String direction,
      @RequestParam(name = "userId", required = false) UUID userId,
      @RequestParam(name = "bookId", required = false) UUID bookId,
      @RequestParam(name = "cursor", required = false) String cursor,
      @RequestParam(name = "after", required = false) String after,
      @RequestParam(name = "limit", required = false) Integer limit) {

      ReviewSearchRequestDto requestDto = ReviewSearchRequestDto.builder()
          .keyword(keyword)
          .orderBy(orderBy)
          .direction(direction)
          .userId(userId)
          .bookId(bookId)
          .cursor(cursor)
          .after(after != null ? Instant.parse(after) : null)
          .limit(limit)
          .build();

      // 서비스 호출
      CursorPageResponseReviewDto result = reviewService.findReviews(requestDto);
      return ResponseEntity.ok(result);
  }
}