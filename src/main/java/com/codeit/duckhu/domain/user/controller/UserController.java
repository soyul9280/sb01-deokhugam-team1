package com.codeit.duckhu.domain.user.controller;

import com.codeit.duckhu.domain.user.controller.api.UserApi;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.ForbiddenUpdateException;
import com.codeit.duckhu.domain.user.service.UserService;
import com.codeit.duckhu.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

  private final UserService userService;

  @Override
  @PostMapping
  public ResponseEntity<UserDto> create(
      @Valid @RequestBody UserRegisterRequest userRegisterRequest) {
    UserDto result = userService.create(userRegisterRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Override
  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
    UserDto result = userService.login(userLoginRequest);
    return ResponseEntity.ok()
        .header("Deokhugam-Request-User-Id", result.getId().toString())
        .body(result);
  }

  @Override
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
    UserDto result = userService.findById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @Override
  @PatchMapping("/{userId}")
  public ResponseEntity<UserDto> update(
      HttpServletRequest request,
      @PathVariable("userId") UUID targetId,
      @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (authenticatedUser == null) { // 로그인 하지 않은 사용자가 들어왔을때
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_UPDATE);
    }
    if (!targetId.equals(authenticatedUser.getId())) { // 로그인은 했지만 권한이 없을때 403 던지기
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_UPDATE);
    }
    UserDto result = userService.update(targetId, userUpdateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @Override
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> softDelete(
      HttpServletRequest request, @PathVariable("userId") UUID targetId) {
    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (authenticatedUser == null) { // 로그인 하지 않은 사용자가 들어왔을때
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    if (!targetId.equals(authenticatedUser.getId())) { // 로그인은 했지만 권한이 없을때 403 던지기
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    userService.softDelete(targetId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  @DeleteMapping("/{userId}/hard")
  public ResponseEntity<Void> hardDelete(
      HttpServletRequest request, @PathVariable("userId") UUID targetId) {
    User authenticatedUser = (User) request.getAttribute("authenticatedUser");
    if (authenticatedUser == null) { // 로그인 하지 않은 사용자가 들어왔을때
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    if (!targetId.equals(authenticatedUser.getId())) { // 로그인은 했지만 권한이 없을때 403 던지기
      throw new ForbiddenUpdateException(ErrorCode.UNAUTHORIZED_DELETE);
    }
    userService.hardDelete(targetId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
