package com.codeit.duckhu.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Custom Error 지정 필요성이 있다면 에러 코드를 작성하여 진행.
 * <p>
 * 일반적인 상황의 에러만 적혀있지만, 특정 도메인에서 발생할 수 있는 에러 등 정의\n\n
 * CustomException(ErrorCode.INTERNAL_SERVER_ERROR) 처럼 사용.
 */
@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {
  // 리뷰 에러
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.", "리뷰를 찾을 수 없습니다."),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.", "리뷰를 찾을 수 없습니다."),
  REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 작성한 리뷰가 존재합니다.", "한 도서에 리뷰는 1개만 작성할 수 있습니다.");


  private final HttpStatus status;
  private final String message;
  private final String detail;
}
