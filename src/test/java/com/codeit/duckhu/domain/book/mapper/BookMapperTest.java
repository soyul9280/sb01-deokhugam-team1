package com.codeit.duckhu.domain.book.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.entity.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

public class BookMapperTest {

  BookMapper mapper = Mappers.getMapper(BookMapper.class);

  private Book book;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .title("Clean Code")
            .author("Robert C. Martin")
            .description("A handbook of agile software craftsmanship")
            .publisher("Prentice Hall")
            .publishedDate(LocalDate.of(2008, 8, 1))
            .isbn("9780132350884")
            .thumbnailUrl("https://example.com/image.jpg")
            .isDeleted(false)
            .build();

    ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(book, "createdAt", Instant.now());
    ReflectionTestUtils.setField(book, "updatedAt", Instant.now());
  }

  @Test
  @DisplayName("Book → BookDto 변환이 올바르게 수행된다")
  void testBookToBookDto() {
    // given
    int reviewCount = 42;
    double rating = 4.7;

    // when
    BookDto dto = mapper.toDto(book, reviewCount, rating);

    // then
    assertThat(dto.id()).isEqualTo(book.getId());
    assertThat(dto.title()).isEqualTo(book.getTitle());
    assertThat(dto.author()).isEqualTo(book.getAuthor());
    assertThat(dto.reviewCount()).isEqualTo(reviewCount);
    assertThat(dto.rating()).isEqualTo(rating);
  }
}
