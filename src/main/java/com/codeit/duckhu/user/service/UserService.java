package com.codeit.duckhu.user.service;

import com.codeit.duckhu.user.dto.UserDto;
import com.codeit.duckhu.user.dto.UserRegisterRequest;

public interface UserService {
    UserDto register(UserRegisterRequest userRegisterRequest);
}
