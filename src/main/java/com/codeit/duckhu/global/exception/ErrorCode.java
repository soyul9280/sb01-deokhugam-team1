package com.codeit.duckhu.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Custom Error 지정 필요성이 있다면 에러 코드를 작성하여 진행.
 *
 * <p>일반적인 상황의 에러만 적혀있지만, 특정 도메인에서 발생할 수 있는 에러 등 정의\n\n
 * CustomException(ErrorCode.INTERNAL_SERVER_ERROR) 처럼 사용.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // COMMON
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.", "관리자에게 연락해 주세요."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다.", "잘못된 요청을 진행하였습니다."),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.", "잘못된 요청을 진행하였습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "잘못된 요청을 진행하였습니다."),

  // BOOK
  DUPLICATE_ISBN(HttpStatus.CONFLICT, "이미 존재하는 ISBN입니다.", "잘못된 요청을 진행하였습니다."),
  INVALID_ISBN_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 ISBN 형식입니다.", "잘못된 요청을 진행하였습니다."),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "ISBN으로 책 정보를 찾을 수 없습니다.", "정보를 찾을 수 없습니다"),
  UNABLE_EXTRACT_ISBN(HttpStatus.BAD_REQUEST, "이미지에서 ISBN을 확인할 수 없습니다.", "잘못된 요청을 진행하였습니다."),
  INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 이미지 형식입니다.", "잘못된 요청을 진행하였습니다."),

  // USER
  EMAIL_DUPLICATION(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.", "USER_409"),
  LOGIN_INPUT_INVALID(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.", "USER_401"),
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다.", "USER_404"),
  UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "사용자 정보 수정 권한 없음", "USER_403"),
  UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "사용자 삭제 권한 없음", "USER_403");

  private final HttpStatus status;
  private final String message;
  private final String detail;
}
