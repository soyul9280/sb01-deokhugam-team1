package com.codeit.duckhu.domain.book.client;

import com.codeit.duckhu.domain.book.dto.NaverApiResponse;
import com.codeit.duckhu.domain.book.dto.NaverApiResponse.Item;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    ResponseEntity<NaverApiResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        request,
        NaverApiResponse.class
    );

    List<Item> items = response.getBody().items();
    if (items == null || items.isEmpty()) {
      throw new RuntimeException("ISBN으로 책 정보를 찾을 수 없습니다.");
    }

    NaverApiResponse.Item item = items.get(0); // 첫 번째 결과 사용
    return new NaverBookDto(
        item.title(),
        item.author(),
        item.description(),
        item.publisher(),
        LocalDate.parse(item.pubdate(), DateTimeFormatter.ofPattern("yyyyMMdd")),
        isbn,
        item.image()
    );
  }
}

