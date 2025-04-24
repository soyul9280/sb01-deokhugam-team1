package com.codeit.duckhu.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.duckhu.domain.user.UserAuthenticationFilter;
import com.codeit.duckhu.domain.user.dto.CursorPageResponsePowerUserDto;
import com.codeit.duckhu.domain.user.dto.PowerUserDto;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.domain.user.service.UserService;
import com.codeit.duckhu.global.exception.GlobalExceptionHandler;
import com.codeit.duckhu.global.type.Direction;
import com.codeit.duckhu.global.type.PeriodType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserService userService;

  @MockitoBean private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    UserAuthenticationFilter filter = new UserAuthenticationFilter(userRepository);
    UserController controller = new UserController(userService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .addFilters(filter)
            .build();
  }

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /api/users-성공")
  void create_success() throws Exception {
    // given
    UserRegisterRequest request =
        new UserRegisterRequest("testA@example.com", "testA", "testa1234!");
    UserDto dto = new UserDto(UUID.randomUUID(), "testA@example.com", "testA", Instant.now());
    given(userService.create(any(UserRegisterRequest.class))).willReturn(dto);

    // when
    // then
    mockMvc
        .perform(
            post("/api/users")
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
    // given
    UserRegisterRequest request = new UserRegisterRequest(null, "testA", null);

    // when
    // then
    mockMvc
        .perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."))
        .andExpect(jsonPath("$.error.details").value("잘못된 요청을 진행하였습니다."));
  }

  @Test
  @DisplayName("POST /api/users/login-성공")
  void login_success() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest("testA@example.com", "testA");
    UserDto dto = new UserDto(UUID.randomUUID(), "testA@example.com", "testA", Instant.now());
    given(userService.login(any(UserLoginRequest.class))).willReturn(dto);

    // when
    // then
    mockMvc
        .perform(
            post("/api/users/login")
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
    // given
    UserLoginRequest request = new UserLoginRequest(null, "testA1!!!!");
    // when
    // then
    mockMvc
        .perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."))
        .andExpect(jsonPath("$.error.details").value("잘못된 요청을 진행하였습니다."));
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 입력값 검증 실패")
  void update_fail() throws Exception {
    // given
    UUID loginId = UUID.randomUUID();
    given(userRepository.findById(loginId))
        .willReturn(Optional.of(new User("test@example.com", "test", "test1234!")));
    UserUpdateRequest request = new UserUpdateRequest("u");
    // when
    // then
    mockMvc
        .perform(
            patch("/api/users/{userId}", loginId)
                .header("Deokhugam-Request-User-ID", loginId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."))
        .andExpect(jsonPath("$.error.details").value("잘못된 요청을 진행하였습니다."));
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 권한없음 (실패)")
  void update_userForbidden_fail() throws Exception {
    // given
    UUID targetId = UUID.randomUUID(); // 타겟(다른 사람)
    UUID loginId = UUID.randomUUID();
    given(userRepository.findById(loginId))
        .willReturn(Optional.of(new User("test@example.com", "test", "test1234!")));

    UserUpdateRequest request = new UserUpdateRequest("newName");

    // when
    // then
    mockMvc
        .perform(
            patch("/api/users/{userId}", targetId)
                .header("Deokhugam-Request-User-ID", loginId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.details").value("USER_403"))
        .andExpect(jsonPath("$.error.message").value("사용자 정보 수정 권한 없음"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - 권한없음 (실패)")
  void softDelete_fail() throws Exception {
    // given
    UUID targetId = UUID.randomUUID(); // 타겟(다른 사람)
    UUID loginId = UUID.randomUUID();
    given(userRepository.findById(loginId))
        .willReturn(Optional.of(new User("test@example.com", "test", "test1234!")));

    // when
    // then
    mockMvc
        .perform(
            delete("/api/users/{userId}", targetId)
                .header("Deokhugam-Request-User-ID", loginId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.details").value("USER_403"))
        .andExpect(jsonPath("$.error.message").value("사용자 삭제 권한 없음"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId}/hard - 권한없음 (실패)")
  void hardDelete_fail() throws Exception {
    // given
    UUID targetId = UUID.randomUUID(); // 타겟(다른 사람)
    UUID loginId = UUID.randomUUID();
    given(userRepository.findById(loginId))
        .willReturn(Optional.of(new User("test@example.com", "test", "test1234!")));

    // when
    // then
    mockMvc
        .perform(
            delete("/api/users/{userId}/hard", targetId)
                .header("Deokhugam-Request-User-ID", loginId)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.details").value("USER_403"))
        .andExpect(jsonPath("$.error.message").value("사용자 삭제 권한 없음"));
  }

  @Test
  @DisplayName("GET api/users/power - 성공")
  void powerUser_success() throws Exception {
    // given
    PeriodType period = PeriodType.DAILY;
    Direction direction = Direction.ASC;
    String cursor = null;
    Instant after = null;
    int limit = 10;

    PowerUserDto dto = PowerUserDto.builder()
            .userId(UUID.randomUUID())
            .nickname("testA")
            .period("DAILY")
            .createdAt(Instant.now())
            .rank(1)
            .score(5.0)
            .reviewScoreSum(3.5)
            .likeCount(2)
            .commentCount(4)
            .build();

    CursorPageResponsePowerUserDto responseDto = CursorPageResponsePowerUserDto.builder()
            .content(List.of(dto))
            .nextCursor(null)
            .nextAfter(null)
            .size(1)
            .totalElements(1)
            .hasNext(false)
            .build();

    given(userService.findPowerUsers(
            eq(period),
            eq(direction),
            isNull(),
            isNull(),
            eq(limit)))
            .willReturn(responseDto);

    // when & then
    mockMvc.perform(MockMvcRequestBuilders.get("/api/users/power")
            .param("period", period.name())
            .param("direction", direction.name())
            .param("limit", String.valueOf(limit))
            .contentType(MediaType.APPLICATION_JSON))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].nickname").value("testA"))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.hasNext").value(false));
  }
}


