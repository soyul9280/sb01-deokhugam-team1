package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.exception.BookException;
import com.codeit.duckhu.domain.book.mapper.BookMapper;
import com.codeit.duckhu.domain.book.naver.NaverBookClient;
import com.codeit.duckhu.domain.book.ocr.OcrExtractor;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.storage.ThumbnailImageStorage;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

  @Mock
  private BookRepository bookRepository;

  @Mock
  private BookMapper bookMapper;

  @Mock
  private ThumbnailImageStorage thumbnailImageStorage;

  @Mock
  private NaverBookClient naverBookClient;

  @Mock
  private OcrExtractor ocrExtractor;

  @InjectMocks
  private BookServiceImpl bookService;

  @Nested
  @DisplayName("도서 저장")
  class SaveBookTest {

    @Test
    @DisplayName("도서 등록 성공 - 썸네일 있음")
    void registerBook_withThumbnail() {
      // given
      BookCreateRequest request = new BookCreateRequest(
          "Effective Java", "Joshua Bloch", "Best practices", "Addison-Wesley",
          LocalDate.of(2018, 1, 1), "9780134685991"
      );

      MultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg",
          "image-content".getBytes());
      String thumbnailUrl = "https://s3.com/image.jpg";

      Book savedBook = Book.builder()
          .title(request.title())
          .author(request.author())
          .description(request.description())
          .publisher(request.publisher())
          .publishedDate(request.publishedDate())
          .isbn(request.isbn())
          .thumbnailUrl(thumbnailUrl)
          .build();

      BookDto expectedDto = new BookDto(
          UUID.randomUUID(), request.title(), request.author(), request.description(),
          request.publisher(), request.publishedDate(), request.isbn(),
          thumbnailUrl, 0, 0.0, Instant.now(), Instant.now()
      );

      given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
      given(thumbnailImageStorage.upload(file)).willReturn(thumbnailUrl);
      given(bookRepository.save(any(Book.class))).willReturn(savedBook);
      given(bookMapper.toDto(any(Book.class), eq(0), eq(0.0))).willReturn(expectedDto);

      // when
      BookDto result = bookService.registerBook(request, Optional.of(file));

      // then
      assertThat(result).isEqualTo(expectedDto);
      verify(bookRepository).save(any(Book.class));
      verify(thumbnailImageStorage).upload(file);
    }

    @Test
    @DisplayName("도서 등록 성공 - 썸네일 없음")
    void registerBook_withoutThumbnail_success() {
      // given
      BookCreateRequest request = new BookCreateRequest(
          "Clean Code", "Robert C. Martin", "Clean coding principles", "Prentice Hall",
          LocalDate.of(2008, 8, 1), "9780132350884"
      );

      Book savedBook = Book.builder()
          .title(request.title())
          .author(request.author())
          .description(request.description())
          .publisher(request.publisher())
          .publishedDate(request.publishedDate())
          .isbn(request.isbn())
          .thumbnailUrl(null)
          .build();

      BookDto expectedDto = new BookDto(
          UUID.randomUUID(), request.title(), request.author(), request.description(),
          request.publisher(), request.publishedDate(), request.isbn(),
          null, 0, 0.0, Instant.now(), Instant.now()
      );

      given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
      given(bookRepository.save(any(Book.class))).willReturn(savedBook);
      given(bookMapper.toDto(any(Book.class), eq(0), eq(0.0))).willReturn(expectedDto);

      // when
      BookDto result = bookService.registerBook(request, Optional.empty());

      // then
      assertThat(result).isEqualTo(expectedDto);
      verify(bookRepository).save(any(Book.class));
      verify(thumbnailImageStorage, never()).upload(any());
    }

    @Test
    @DisplayName("ISBN 중복 시 예외 발생")
    void registerBook_duplicateIsbn_exceptionThrown() {
      // given
      BookCreateRequest request = new BookCreateRequest(
          "토비 스프링", "토비", "스프링의 교과서", "Test",
          LocalDate.of(2003, 8, 30), "9780321125217"
      );

      given(bookRepository.existsByIsbn(request.isbn())).willReturn(true);

      // when & then
      assertThatThrownBy(() -> bookService.registerBook(request, Optional.empty()))
          .isInstanceOf(BookException.class);

      verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("ISBN 형식에 맞지 않으면 예외 발생")
    void registerBook_invalidIsbn_exceptionThrown() {
      //given
      BookCreateRequest request = new BookCreateRequest(
          "Invalid Book", "Author", "Invalid ISBN format", "Publisher",
          LocalDate.of(2020, 1, 1), "INVALID_ISBN"
      );

      //when & then
      assertThatThrownBy(() -> bookService.registerBook(request, Optional.empty()))
          .isInstanceOf(BookException.class);

      verify(bookRepository, never()).save(any());
    }
  }
}
