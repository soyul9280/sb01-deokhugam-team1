package com.codeit.duckhu.domain.user.integration;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 생성-성공")
    @Transactional
    void create_success() throws Exception {
        //given
        UserRegisterRequest request = new UserRegisterRequest(
                "testA@example.com", "testA", "testa1234!"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = objectMapper.writeValueAsString(request);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        //when
        ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", requestEntity, UserDto.class);

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("testA", response.getBody().getNickname());
        assertEquals("testA@example.com", response.getBody().getEmail());
    }

    //로그인은 Security설정 때문에 나중에 고려하기

    @Test
    @DisplayName("사용자 상세 조회- 성공")
    @Sql("/data.sql")
    void find_success() throws Exception {
        //given
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        //when
        ResponseEntity<UserDto> response = restTemplate.getForEntity("/api/users/" + id, UserDto.class);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("테스트유저", response.getBody().getNickname());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("사용자 정보 수정")

}

