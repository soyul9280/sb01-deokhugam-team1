package com.codeit.duckhu.domain.book.client;

import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/*
TestPropertySource에 직접 clientId와 secret 값을 입력하여 테스트 성공을 확인함
 */
@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
    "naver.client-id={client-id}}",
    "naver.client-secret={secret}}"
})
class NaverBookClientIntegrationTest {

  @Autowired
  private NaverBookClient naverBookClient;

  @Test
  @DisplayName("네이버 OpenAPI를 통해 ISBN으로 실제 책 정보를 조회할 수 있다")
  void searchByIsbn_realApiCall_success() {
    // given
    String isbn = "9788966260959"; // 클린코드 ISBN

    // when
    NaverBookDto result = naverBookClient.searchByIsbn(isbn);

    // then
    assertThat(result).isNotNull();
    assertThat(result.title()).contains("클린 코드");
    assertThat(result.author()).contains("로버트 C. 마틴");
    assertThat(result.publishedDate()).isEqualTo(LocalDate.of(2013, 12, 24));
    assertThat(result.thumbnailImage()).contains("https://");
  }
}
