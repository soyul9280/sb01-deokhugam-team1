package com.codeit.duckhu.user.service;

import com.codeit.duckhu.user.dto.UserDto;
import com.codeit.duckhu.user.dto.UserRegisterRequest;
import com.codeit.duckhu.user.entity.User;
import com.codeit.duckhu.user.exception.EmailDuplicateException;
import com.codeit.duckhu.user.mapper.UserMapper;
import com.codeit.duckhu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto register(UserRegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailDuplicateException(request.getEmail());
        }

        User user = new User(
                UUID.randomUUID(),
                request.getEmail(),
                request.getNickname(),
                request.getPassword(),
                Instant.now());

        User savedUser=userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
