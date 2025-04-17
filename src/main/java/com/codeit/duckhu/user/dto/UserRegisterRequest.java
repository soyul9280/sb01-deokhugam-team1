package com.codeit.duckhu.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserRegisterRequest {
    private String email;
    private String nickname;
    private String password;
}
