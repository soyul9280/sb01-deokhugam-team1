package com.codeit.duckhu.user.repository;

import com.codeit.duckhu.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("저장 성공")
    void save_success() {
        //given
        User user=new User("testA@example.com", "testA", "testa1234!", false);

        //when
        User result = userRepository.save(user);

        //then
        assertThat(result.getEmail()).isEqualTo("testA@example.com");
        assertThat(result.getNickname()).isEqualTo("testA");
        assertThat(result.getPassword()).isEqualTo("testa1234!");
    }

    @Test
    @DisplayName("저장 실패")
    void save_fail() {
        //given
        User user=new User(null, null, "testa1234!", false);
        //when
        //then
        assertThatThrownBy(() -> {
            userRepository.save(user);
            userRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("이메일 존재 여부 성공")
    void existsByEmail_success() {
        //given
        userRepository.save(new User("testA@example.com", "testA", "testa1234!", false));
        //when
        //then
        assertThat(userRepository.existsByEmail("testA@example.com")).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 실패")
    void existsByEmail_fail() {
        //given
        userRepository.save(new User("testA@example.com", "testA", "testa1234!", false));
        //when
        //then
        assertThat(userRepository.existsByEmail("testB@example.com")).isFalse();
    }




}
