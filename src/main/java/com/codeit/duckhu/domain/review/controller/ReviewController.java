package com.codeit.duckhu.domain.review.controller;

import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  private ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewCreateRequest request) {
    ReviewDto review = reviewService.createReview(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(review);
  }

  @PatchMapping("/{reviewId}")
  private ResponseEntity<ReviewDto> updateReview(
      @PathVariable("reviewId") UUID reviewId,
      @Valid @RequestBody ReviewUpdateRequest request) {
    ReviewDto review = reviewService.updateReview(reviewId, request);
    return ResponseEntity.status(HttpStatus.OK).body(review);
  }
}
