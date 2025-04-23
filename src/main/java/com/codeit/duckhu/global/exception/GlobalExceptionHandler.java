package com.codeit.duckhu.global.exception;

import com.codeit.duckhu.global.response.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/** 전역 예외 핸들러 - 전역 예외 처리를 위함. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 존재하지 않는 요청에 대한 예외.
   *
   * @param e
   * @return 500 INTERNAL SERVER ERROR 응답
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<CustomApiResponse<?>> handleNoHandlerFound(NoHandlerFoundException ex) {
    log.warn("요청한 페이지 없음: {}", ex.getRequestURL());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(CustomApiResponse.fail(new CustomException(ErrorCode.NOT_FOUND)));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<CustomApiResponse<?>> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex) {
    log.warn("지원하지 않는 HTTP Method: {}", ex.getMethod());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(CustomApiResponse.fail(new CustomException(ErrorCode.METHOD_NOT_ALLOWED)));
  }

  /**
   * 요청값 검증 실패
   *
   * @param e
   * @return 400 INVALID_INPUT_VALUE 응답
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<CustomApiResponse<?>> handleValidationException(
      MethodArgumentNotValidException e) {
    log.error("Validation failed: {}", e.getMessage());

    CustomApiResponse<?> response =
        CustomApiResponse.fail(new CustomException(ErrorCode.INVALID_INPUT_VALUE));

    return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()) // 400
        .body(response);
  }

  /**
   * 커스텀 예외에 대한 처리.
   *
   * @param e
   * @return 해당 Error 코드에 대응하는 에러 응답
   */
  @ExceptionHandler(value = {CustomException.class})
  public ResponseEntity<CustomApiResponse<?>> handleCustomException(CustomException e) {
    log.error(
        "handleCustomException() in GlobalExceptionHandler throw CustomException : {}",
        e.getMessage());
    return ResponseEntity.status(e.getErrorCode().getStatus()) // ex. 403, 409, 401
        .body(CustomApiResponse.fail(e));
  }

  /**
   * 기본적인 예외에 대한 처리
   *
   * @param e
   * @return 500 INTERNAL SERVER ERROR 응답
   */
  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<CustomApiResponse<?>> handleException(Exception e) {
    log.error("handleException() in GlobalExceptionHandler throw Exception : {}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(CustomApiResponse.fail(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR)));
  }
}
