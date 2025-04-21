package com.codeit.duckhu.domain.user.controller;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.exception.UserExceptionHandler;
import com.codeit.duckhu.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserExceptionHandler.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/users-성공")
    void create_success() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "testA@example.com", "testA", "testa1234!"
        );
        UserDto dto = new UserDto(UUID.randomUUID(), "testA@example.com", "testA", Instant.now());
        given(userService.create(any(UserRegisterRequest.class))).willReturn(dto);

        //when
        //then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("testA@example.com"))
                .andExpect(jsonPath("$.nickname").value("testA"));
    }

    @Test
    @DisplayName("POST /api/users-실패")
    void create_fail() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                null, "testA", null
        );

        //when
        //then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.code").value("InvalidMethodArgumentException"))
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("POST /api/users/login-성공")
    void login_success() throws Exception {
        //given
        UserLoginRequest request = new UserLoginRequest("testA@example.com", "testA");
        UserDto dto = new UserDto(UUID.randomUUID(), "testA@example.com", "testA", Instant.now());
        given(userService.login(any(UserLoginRequest.class))).willReturn(dto);

        //when
        //then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("testA@example.com"))
                .andExpect(jsonPath("$.nickname").value("testA"));
    }

    @Test
    @DisplayName("POST /api/users/login-실패")
    void login_fail() throws Exception {
        //given
        UserLoginRequest request = new UserLoginRequest(
                null, "testA1!!!!"
        );
        //when
        //then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.code").value("InvalidMethodArgumentException"))
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 입력값 검증 실패")
    void update_fail() throws Exception {
        //given
        UserUpdateRequest request = new UserUpdateRequest("u");
        //when
        //then
        mockMvc.perform(patch("/api/users/{userId}",UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())

                .andExpect(jsonPath("$.code").value("InvalidMethodArgumentException"))
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.details.nickname").value("닉네임은 2자 이상 20자 이하로 입력해주세요."));
    }



}
