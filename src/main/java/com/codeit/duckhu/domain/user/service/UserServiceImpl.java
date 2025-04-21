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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserRegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailDuplicateException(request.getEmail());
        }

        User user = new User(
                request.getEmail(),
                request.getNickname(),
                request.getPassword(),
                false);

        User savedUser=userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto login(UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();
        String password = userLoginRequest.getPassword();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidLoginException(email));
        if(!user.getPassword().equals(password)) {
            throw new InvalidLoginException(email);
        }
        return userMapper.toDto(user);
    }

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserException(id));
        if (user.isDeleted()) {
            throw new NotFoundUserException(id);
        }
        return userMapper.toDto(user);
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundUserException(id));
        if (user.isDeleted()) {
            throw new NotFoundUserException(id);
        }
        user.update(userUpdateRequest);
        return userMapper.toDto(user);
    }
}
