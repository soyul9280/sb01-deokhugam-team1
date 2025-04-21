package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {
    UserDto create(UserRegisterRequest userRegisterRequest);
    UserDto login(UserLoginRequest userLoginRequest);
    UserDto findById(UUID id);
    UserDto update(UUID id, UserUpdateRequest userUpdateRequest);
}
