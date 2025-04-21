package com.codeit.duckhu.global.exception;

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
public enum ErrorCode {
  // COMMON
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.", "관리자에게 연락해 주세요."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "잘못된 요청을 진행하였습니다."),

  // BOOK
  DUPLICATE_ISBN(HttpStatus.CONFLICT, "이미 존재하는 ISBN입니다.", "잘못된 요청을 진행하였습니다."),
  INVALID_ISBN_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 ISBN 형식입니다.", "잘못된 요청을 진행하였습니다."),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "ISBN으로 책 정보를 찾을 수 없습니다.", "정보를 찾을 수 없습니다"),
  UNABLE_EXTRACT_ISBN(HttpStatus.BAD_REQUEST, "이미지에서 ISBN을 확인할 수 없습니다.", "잘못된 요청을 진행하였습니다."),
  INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이미지 형식입니다.", "잘못된 요청을 진행하였습니다.");

  private final HttpStatus status;
  private final String message;
  private final String detail;
}
