package com.codeit.duckhu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private UUID id;
    private String email;
    private String nickname;
    private Instant createdAt;
}
