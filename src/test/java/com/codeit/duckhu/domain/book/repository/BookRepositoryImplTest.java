package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.review.repository.TestJpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.global.type.Direction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


@ActiveProfiles("test")
@DataJpaTest
@Import({BookRepositoryImpl.class, TestJpaConfig.class})
public class BookRepositoryImplTest {

  @Autowired
  private BookRepositoryImpl bookRepositoryImpl;

  @Autowired
  private BookRepository bookRepository;

  @PersistenceContext
  private EntityManager em;

  private Book book1, book2, book3;

  @BeforeEach
  void setUp() {
    // 기본 테스트용 데이터 3개 저장
    book1 = Book.builder()
        .title("Effective Java")
        .author("Joshua Bloch")
        .isbn("9780134685991")
        .description("Best practices")
        .publisher("Addison-Wesley")
        .publishedDate(LocalDate.of(2018, 1, 6))
        .isDeleted(false)
        .build();

    book2 = Book.builder()
        .title("Clean Code")
        .author("Robert C. Martin")
        .isbn("9780132350884")
        .description("Clean code guide")
        .publisher("Prentice Hall")
        .publishedDate(LocalDate.of(2008, 8, 1))
        .isDeleted(false)
        .build();

    book3 = Book.builder()
        .title("Spring in Action")
        .author("Craig Walls")
        .isbn("9781617294945")
        .description("Spring framework")
        .publisher("Manning")
        .publishedDate(LocalDate.of(2018, 8, 1))
        .isDeleted(false)
        .build();

    bookRepository.saveAll(List.of(book1, book2, book3));
    em.flush();
    em.clear();
  }

  @Nested
  @DisplayName("도서 검색")
  class SearchBooks {

    @Test
    @DisplayName("키워드 검색 - 제목으로 검색 성공")
    void searchByTitle() {
      // When
      List<Book> result = bookRepositoryImpl.searchBooks(
          "Effective",
          "title",
          Direction.ASC,
          null,
          null,
          10
      );

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    @DisplayName("키워드 검색 - 저자로 검색 성공")
    void searchByAuthor() {
      // When
      List<Book> result = bookRepositoryImpl.searchBooks(
          "Robert",
          "title",
          Direction.ASC,
          null,
          null,
          10
      );

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    @DisplayName("키워드 검색 - ISBN으로 검색 성공")
    void searchByIsbn() {
      // When
      List<Book> result = bookRepositoryImpl.searchBooks(
          "9781617294945",
          "title",
          Direction.ASC,
          null,
          null,
          10
      );

      // Then
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getTitle()).isEqualTo("Spring in Action");
    }

    @Test
    @DisplayName("정렬 테스트 - 제목 오름차순 정렬")
    void sortByTitleAsc() {
      // When
      List<Book> result = bookRepositoryImpl.searchBooks(
          "",
          "title",
          Direction.ASC,
          null,
          null,
          10
      );

      // Then
      assertThat(result).hasSize(3);
      assertThat(result.get(0).getTitle()).isEqualTo("Clean Code");
      assertThat(result.get(1).getTitle()).isEqualTo("Effective Java");
      assertThat(result.get(2).getTitle()).isEqualTo("Spring in Action");
    }

    @Test
    @DisplayName("정렬 테스트 - 제목 내림차순 정렬")
    void sortByTitleDesc() {
      // When
      List<Book> result = bookRepositoryImpl.searchBooks(
          "",
          "title",
          Direction.DESC,
          null,
          null,
          10
      );

      // Then
      assertThat(result).hasSize(3);
      assertThat(result.get(0).getTitle()).isEqualTo("Spring in Action");
      assertThat(result.get(1).getTitle()).isEqualTo("Effective Java");
      assertThat(result.get(2).getTitle()).isEqualTo("Clean Code");
    }
  }

  @Nested
  @DisplayName("커서 기반 페이지네이션 테스트")
  class CursorPagingTest {

    @Test
    @DisplayName("rating 기준 커서 페이징")
    void cursorPagingByRating() {
      // Given
      List<Book> books = bookRepositoryImpl.searchBooks(
          "", "rating", Direction.ASC,
          "4.5", book1.getCreatedAt(), 10
      );

      // Then
      assertThat(books)
          .extracting(Book::getRating)
          .allSatisfy(rating -> assertThat(rating).isGreaterThanOrEqualTo(4.5));
    }

    @Test
    @DisplayName("reviewCount 기준 커서 페이징")
    void cursorPagingByReviewCount() {
      // Given
      List<Book> books = bookRepositoryImpl.searchBooks(
          "", "reviewCount", Direction.ASC,
          "10", book1.getCreatedAt(), 10
      );

      // Then
      assertThat(books)
          .extracting(Book::getReviewCount)
          .allSatisfy(reviewCount -> assertThat(reviewCount).isGreaterThanOrEqualTo(10));
    }

    @Test
    @DisplayName("publishedDate 기준 커서 페이징")
    void cursorPagingByPublishedDate() {
      // Given
      List<Book> books = bookRepositoryImpl.searchBooks(
          "", "publishedDate", Direction.ASC,
          book1.getPublishedDate().toString(), book1.getCreatedAt(), 10
      );

      // Then
      assertThat(books)
          .extracting(Book::getPublishedDate)
          .allSatisfy(date -> assertThat(date).isAfterOrEqualTo(book1.getPublishedDate()));
    }
  }

}
