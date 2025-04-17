package com.codeit.duckhu.user.mapper;

import com.codeit.duckhu.user.dto.UserDto;
import com.codeit.duckhu.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
