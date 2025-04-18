package com.codeit.duckhu.domain.book.client;

import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverBookClient {

  private final ObjectMapper objectMapper;
  @Value("${naver.client-id}")
  private String clientId;

  @Value("${naver.client-secret}")
  private String clientSecret;

  private final RestTemplate restTemplate = new RestTemplate();

  public NaverBookDto searchByIsbn(String isbn) {
    String url = "https://openapi.naver.com/v1/search/book_adv.json?d_isbn=" + isbn;

    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Naver-Client-Id", clientId);
    headers.set("X-Naver-Client-Secret", clientSecret);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    ResponseEntity<String> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        String.class
    );

    try {
      JsonNode root = objectMapper.readTree(response.getBody());
      JsonNode itemNode = root.path("items").get(0);

      if (itemNode == null || itemNode.isEmpty())  {
        throw new RuntimeException("ISBN으로 책 정보를 찾을 수 없습니다");
      }

      return new NaverBookDto(
          itemNode.path("title").asText(),
          itemNode.path("author").asText(),
          itemNode.path("description").asText(),
          itemNode.path("publisher").asText(),
          LocalDate.parse(itemNode.path("pubdate").asText(), DateTimeFormatter.ofPattern("yyyyMMdd")),
          isbn,
          itemNode.path("image").asText()
      );
    } catch (Exception e) {
      throw new RuntimeException("Naver 응답 파싱 실패", e);
    }
  }
}
