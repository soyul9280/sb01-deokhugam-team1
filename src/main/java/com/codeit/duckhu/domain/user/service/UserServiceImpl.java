package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.EmailDuplicateException;
import com.codeit.duckhu.domain.user.mapper.UserMapper;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
