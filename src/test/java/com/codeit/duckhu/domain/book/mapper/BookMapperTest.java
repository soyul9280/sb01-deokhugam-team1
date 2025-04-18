package com.codeit.duckhu.domain.book.mapper;

import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(BookMapper.class)
public class BookMapperTest {

  @Autowired
  private BookMapper mapper;

  @Autowired
  private BookRepository bookRepository;

  private Book savedBook;

  @BeforeEach
  void setUp() {
    Book book = Book.builder()
        .title("Clean Code")
        .author("Robert C. Martin")
        .description("A handbook of agile software craftsmanship")
        .publisher("Prentice Hall")
        .publishedDate(LocalDate.of(2008, 8, 1))
        .isbn("9780132350884")
        .thumbnailUrl("https://example.com/image.jpg")
        .build();

    savedBook = bookRepository.save(book);
  }

  @Test
  @DisplayName("Book → BookDto 변환이 올바르게 수행된다")
  void testBookToBookDto() {
    // given
    int reviewCount = 42;
    double rating = 4.7;

    // when
    BookDto dto = mapper.toDto(savedBook, reviewCount, rating);

    // then
    assertThat(dto.id()).isEqualTo(savedBook.getId());
    assertThat(dto.title()).isEqualTo(savedBook.getTitle());
    assertThat(dto.author()).isEqualTo(savedBook.getAuthor());
    assertThat(dto.reviewCount()).isEqualTo(reviewCount);
    assertThat(dto.rating()).isEqualTo(rating);
  }
}
