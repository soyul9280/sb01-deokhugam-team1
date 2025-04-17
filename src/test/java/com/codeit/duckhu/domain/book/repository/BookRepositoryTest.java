package com.codeit.duckhu.domain.book.repository;

import java.time.Instant;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
public class BookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  @Test
  @DisplayName("책 엔티티를 저장하고 조회할 수 있다.")
  void save() {
    Book book = Book.builder()
        .title("Effective Java")
        .author("Joshua Bloch")
        .description("Java best practices")
        .publisher("Addison-Wesley")
        .publishedDate(Instant.now())
        .isbn("9780134685991")
        .thumbnailUrl("https://example.com/thumb.jpg")
        .isDeleted(false)
        .createdAt(Instant.now()) // 또는 Auditing 적용
        .updatedAt(Instant.now())
        .build();

    Book saved = bookRepository.save(book);
    Optional<Book> found = bookRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getTitle()).isEqualTo("Effective Java");
  }
}
