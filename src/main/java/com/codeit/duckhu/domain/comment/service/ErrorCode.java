package com.codeit.duckhu.domain.comment.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  NOT_FOUND_COMMENT(HttpStatus.BAD_REQUEST,"잘못된 요청입니다.", "해당하는 댓글을 찾을 수 없습니다.");


  private final HttpStatus status;
  private final String message;
  private final String detail;
}
