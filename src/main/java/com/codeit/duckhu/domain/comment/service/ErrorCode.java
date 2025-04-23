package com.codeit.duckhu.domain.comment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  NO_USER_IN_HEADER(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "요청 유저를 찾을 수 없습니다."),
  NO_AUTHORITY_USER(HttpStatus.FORBIDDEN, "잘못된 요청입니다.", "해당 유저에게 권한이 없습니다."),
  NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "잘못된 요청입니다.", "해당하는 댓글을 찾을 수 없습니다.");

  private final HttpStatus status;
  private final String message;
  private final String detail;
}