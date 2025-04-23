package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.EmailDuplicateException;
import com.codeit.duckhu.domain.user.exception.InvalidLoginException;
import com.codeit.duckhu.domain.user.exception.NotFoundUserException;
import com.codeit.duckhu.domain.user.mapper.UserMapper;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.global.exception.ErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserDto create(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new EmailDuplicateException(ErrorCode.EMAIL_DUPLICATION);
    }

    User user = new User(request.getEmail(), request.getNickname(), request.getPassword(), false);

    User savedUser = userRepository.save(user);

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto login(UserLoginRequest userLoginRequest) {
    String email = userLoginRequest.getEmail();
    String password = userLoginRequest.getPassword();
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new InvalidLoginException(ErrorCode.LOGIN_INPUT_INVALID));
    if (!user.getPassword().equals(password)) {
      throw new InvalidLoginException(ErrorCode.LOGIN_INPUT_INVALID);
    }
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    }
    return userMapper.toDto(user);
  }

  @Override
  public UserDto update(UUID id, UserUpdateRequest userUpdateRequest) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    }
    user.update(userUpdateRequest);
    return userMapper.toDto(user);
  }

  public User findByIdEntityReturn(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new NotFoundUserException(ErrorCode.NOT_FOUND_USER);
    }

    return user;
  }

  @Override
  public void softDelete(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
    user.softDelete();
  }

  @Override
  public void hardDelete(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
    userRepository.deleteById(user.getId());
  }
}
