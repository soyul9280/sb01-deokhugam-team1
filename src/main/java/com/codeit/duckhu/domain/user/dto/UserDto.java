package com.codeit.duckhu.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private final UUID id;
    private final String email;
    private final String nickname;
    private final Instant createdAt;
}
