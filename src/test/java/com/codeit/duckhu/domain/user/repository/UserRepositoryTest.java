package com.codeit.duckhu.domain.user.repository;

import com.codeit.duckhu.config.JpaConfig;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class})
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

    @Test
    @DisplayName("email로 사용자 조회 성공")
    void findByNickname_success() {
        //given
        User user = new User("testA@example.com", "testA", "testa1234!", false);
        userRepository.saveAndFlush(user);

        //when
        Optional<User> findUser = userRepository.findByEmail("testA@example.com");

        //then
        assertThat(findUser).isPresent();
        assertThat(findUser.get().getNickname()).isEqualTo("testA");
    }

    @Test
    @DisplayName("email로 사용자 조회 실패")
    void findByNickname_fail() {
        //given
        String nonExistEmail="te@example.com";

        //when
        Optional<User> findUser = userRepository.findByEmail(nonExistEmail);

        //then
        assertThat(findUser).isEmpty();
    }



}
