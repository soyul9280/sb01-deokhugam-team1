package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.EmailDuplicateException;
import com.codeit.duckhu.domain.user.mapper.UserMapper;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl sut;

    @Nested
    @DisplayName("사용자 회원가입 테스트")
    class RegisterUserTest {
        @Test
        @DisplayName("회원가입 성공")
        void register_success() {
            //given
            UUID id = UUID.randomUUID();
            Instant now = Instant.now();
            UserRegisterRequest request = new UserRegisterRequest(
                    "testA@example.com", "testA", "testa1234!"
            );
            User user = new User( "testA@example.com", "testA", "testa1234!", false);
            UserDto dto = new UserDto(id, "testA@example.com", "testA", now);
            given(userRepository.existsByEmail("testA@example.com")).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(user);
            given(userMapper.toDto(any(User.class))).willReturn(dto);

            //when
            UserDto result = sut.create(request);

            //then
            assertThat(result.getEmail()).isEqualTo("testA@example.com");
            assertThat(result.getNickname()).isEqualTo("testA");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 이메일")
        void register_fail() {
            //given
            UserRegisterRequest dto = new UserRegisterRequest("testA@example.com", "testA", "testa1234!");
            given(userRepository.existsByEmail("testA@example.com")).willReturn(true);

            //when
            //then
            assertThatThrownBy(() -> sut.create(dto)).isInstanceOf(EmailDuplicateException.class);
        }
    }

}
