package com.codeit.duckhu.domain.user.integration;

import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
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
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("사용자 조회- 성공")
    @Transactional
    void find_success() throws Exception {

    }

}
