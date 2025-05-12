package com.codeit.duckhu.domain.user.controller;

import com.codeit.duckhu.domain.user.controller.api.UserApi;
import com.codeit.duckhu.domain.user.dto.CursorPageResponsePowerUserDto;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.UserException;
import com.codeit.duckhu.domain.user.service.UserService;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.codeit.duckhu.global.type.Direction;
import com.codeit.duckhu.global.type.PeriodType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {

  private final UserService userService;

  @Override
  @PostMapping
  public ResponseEntity<UserDto> create(
      HttpServletRequest request, @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
    log.info("API 호출: POST /api/users 이메일: {}, 닉네임: {}", userRegisterRequest.getEmail(), userRegisterRequest.getNickname());
    UserDto result = userService.create(userRegisterRequest);
    request.getSession(true).setAttribute("userId", result.getId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .header("Deokhugam-Request-User-ID", result.getId().toString())
        .body(result);
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(
      HttpServletRequest request, @Valid @RequestBody UserLoginRequest userLoginRequest) {
    log.info("API 호출: POST /api/users/login 이메일: {}", userLoginRequest.getEmail());

    UserDto result = userService.login(userLoginRequest);
    request.getSession(true).setAttribute("userId", result.getId());
    return ResponseEntity.ok()
        .header("Deokhugam-Request-User-ID", result.getId().toString())
        .body(result);
  }

  @Override
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
    log.info("API 호출: GET /api/users/{userId} id: {}", userId);

    UserDto result = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Deokhugam-Request-User-ID", result.getId().toString())
        .body(result);
  }

  @Override
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
      HttpServletRequest request,
      @PathVariable("userId") UUID targetId,
      @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
    log.info("API 호출: PATCH /api/users/{userId} id: {}", targetId);

    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (!targetId.equals(authenticatedUser.getId())) {
      log.warn("비인증 사용자, 사용자 수정 시도 차단");
      throw new UserException(ErrorCode.UNAUTHORIZED_UPDATE);
    }
    UserDto result = userService.update(targetId, userUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @Override
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> softDelete(
      HttpServletRequest request, @PathVariable("userId") UUID targetId) {
    log.info("API 호출: DELETE /api/users/{userId} id: {}", targetId);

    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (!targetId.equals(authenticatedUser.getId())) {
      log.warn("비인증 사용자, 사용자 논리 삭제 시도 차단");
      throw new UserException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    userService.softDelete(targetId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  @DeleteMapping("/{userId}/hard")
  public ResponseEntity<Void> hardDelete(
      HttpServletRequest request, @PathVariable("userId") UUID targetId) {
    log.info("API 호출: DELETE /api/users/{userId}/hard id: {}", targetId);

    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (!targetId.equals(authenticatedUser.getId())) {
      log.warn("비인증 사용자, 사용자 물리 삭제 시도 차단");
      throw new UserException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    userService.hardDelete(targetId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  @GetMapping("/power")
  public ResponseEntity<CursorPageResponsePowerUserDto> findPowerUsers(
      @RequestParam PeriodType period,
      @RequestParam(defaultValue = "ASC") Direction direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant after,
      @RequestParam(defaultValue = "50") int limit) {
    log.info("API 호출: GET /api/users/power, direction= {}, cursor= {}, limit= {}", direction, cursor, limit);
    CursorPageResponsePowerUserDto result =
        userService.findPowerUsers(period, direction, cursor, after, limit);

    log.info(
            "API 응답 준비 완료: returnedCount={}, hasNext={}",
            result.getContent().size(),
            result.isHasNext());
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }
}
