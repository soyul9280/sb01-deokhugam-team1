package com.codeit.duckhu.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class User {
    private UUID id;
    private String email;
    private String nickname;
    private String password;
    private Instant createdAt;



}
