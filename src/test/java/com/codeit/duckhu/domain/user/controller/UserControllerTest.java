package com.codeit.duckhu.user.controller;

import com.codeit.duckhu.user.dto.UserDto;
import com.codeit.duckhu.user.dto.UserRegisterRequest;
import com.codeit.duckhu.user.exception.UserExceptionHandler;
import com.codeit.duckhu.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserExceptionHandler.class)
@ActiveProfiles("test")
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

}
