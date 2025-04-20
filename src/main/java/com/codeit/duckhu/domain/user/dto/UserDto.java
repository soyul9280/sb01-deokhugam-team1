package com.codeit.duckhu.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter @Builder
public class UserDto {
    private final UUID id;
    private final String email;
    private final String nickname;
    private final Instant createdAt;
}
