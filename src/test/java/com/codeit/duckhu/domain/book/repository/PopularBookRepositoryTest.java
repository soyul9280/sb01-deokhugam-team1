package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.entity.PopularBook;
import com.codeit.duckhu.global.config.AuditingConfig;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
class PopularBookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private PopularBookRepository popularBookRepository;

  /**
   * 인기 도서를 정상적으로 저장하고 조회할 수 있어야 한다.
   */
  @Test
  void should_save_and_retrieve_popular_book() {
    Book book = Book.builder()
        .title("Clean Code")
        .author("Robert C. Martin")
        .description("A Handbook of Agile Software Craftsmanship")
        .publisher("Prentice Hall")
        .publishedDate(Instant.now())
        .isbn("9780132350884")
        .thumbnailUrl("https://example.com/clean.jpg")
        .isDeleted(false)
        .build();

    bookRepository.save(book);

    PopularBook popularBook = PopularBook.builder()
        .book(book)
        .period(PeriodType.MONTHLY)
        .reviewCount(42)
        .rating(4.7)
        .rank(1)
        .score(92.5)
        .build();

    PopularBook saved = popularBookRepository.save(popularBook);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getBook().getTitle()).isEqualTo("Clean Code");
  }
}
