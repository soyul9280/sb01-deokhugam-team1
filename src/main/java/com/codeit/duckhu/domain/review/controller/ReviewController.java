package com.codeit.duckhu.domain.review.controller;

import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.ForbiddenUpdateException;
import com.codeit.duckhu.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<ReviewDto> createReview(
      @Valid @RequestBody ReviewCreateRequest request) {
    ReviewDto review = reviewService.createReview(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(review);
  }

  @PostMapping("/{reviewId}/like")
  public ResponseEntity<ReviewLikeDto> likeReview(
      @PathVariable("reviewId") UUID reviewId,
      HttpServletRequest httpServletRequest) {
    User authenticatedUser = (User) httpServletRequest.getAttribute("authenticatedUser");
    if (authenticatedUser == null) { // 로그인 하지 않은 사용자가 들어왔을때
      log.warn("비인증 사용자 리뷰 좋아요 요청 차단");
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    ReviewLikeDto result = reviewService.likeReview(reviewId, authenticatedUser.getId());
    return ResponseEntity.ok(result);
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> updateReview(
      @RequestParam("userId") UUID userId,
      @PathVariable("reviewId") UUID reviewId,
      @Valid @RequestBody ReviewUpdateRequest request) {
    ReviewDto review = reviewService.updateReview(userId, reviewId, request);
    return ResponseEntity.ok(review);
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> softDeleteReview(
      @RequestParam("userId") UUID userId,
      @PathVariable UUID reviewId) {
    reviewService.softDeleteReviewById(userId, reviewId);
    return ResponseEntity.noContent().build();
  }

  // TODO : 코드레빗, 관리자 권한 ? -> security
  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDeleteReview(
      @RequestParam("userId") UUID userId,
      @PathVariable("reviewId") UUID reviewId) {
    reviewService.hardDeleteReviewById(userId, reviewId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewDto> getReviewById(
      HttpServletRequest httpServletRequest,
      @PathVariable("reviewId") UUID reviewId) {
    User authenticatedUser = (User) httpServletRequest.getAttribute("authenticatedUser");
    if (authenticatedUser == null) { // 로그인 하지 않은 사용자가 들어왔을때
      log.warn("비인증 사용자 리뷰 상세 조회 요청 차단");
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }

    ReviewDto review = reviewService.getReviewById(authenticatedUser.getId(), reviewId);
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
          .after(after != null ? Instant.parse(after) : null) // TODO : 코드레빗 , 예외 처리를 어떻게 할지?
          // limit가 null이면 기본값인 50이 적용됨?
          .limit(limit != null ? limit : 50)
          .build();

    CursorPageResponseReviewDto result = reviewService.findReviews(requestDto);
    return ResponseEntity.ok(result);
  }
}
