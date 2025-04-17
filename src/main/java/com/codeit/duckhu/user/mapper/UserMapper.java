package com.codeit.duckhu.user.mapper;

import com.codeit.duckhu.user.dto.UserDto;
import com.codeit.duckhu.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
