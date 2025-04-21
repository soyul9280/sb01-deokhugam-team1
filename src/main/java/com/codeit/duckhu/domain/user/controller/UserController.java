package com.codeit.duckhu.domain.user.controller;

import com.codeit.duckhu.domain.user.controller.api.UserApi;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.exception.ForbiddenDeleteException;
import com.codeit.duckhu.domain.user.exception.ForbiddenUpdateException;
import com.codeit.duckhu.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserDto result = userService.create(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserDto result = userService.login(userLoginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findById(@PathVariable UUID userId) {
        UserDto result = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestHeader("X-User-Id") UUID loginId, @PathVariable("userId") UUID targetId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        if (!targetId.equals(loginId)) {
            throw new ForbiddenUpdateException(loginId, targetId);
        }
        UserDto result = userService.update(targetId, userUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> softDelete(@RequestHeader("X-User-Id") UUID loginId, @PathVariable("userId") UUID targetId) {
        if (!targetId.equals(loginId)) {
            throw new ForbiddenDeleteException(loginId, targetId);
        }
        userService.softDelete(targetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
