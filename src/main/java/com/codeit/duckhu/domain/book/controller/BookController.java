package com.codeit.duckhu.domain.book.controller;

import com.codeit.duckhu.domain.book.controller.api.BookApi;
import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.BookUpdateRequest;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.dto.CursorPageResponsePopularBookDto;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.codeit.duckhu.domain.book.service.BookService;
import com.codeit.duckhu.global.type.Direction;
import com.codeit.duckhu.global.type.PeriodType;
import jakarta.validation.Valid;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController implements BookApi {

  private final BookService bookService;

  @GetMapping
  public ResponseEntity<CursorPageResponseBookDto> getBooks(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "orderBy", defaultValue = "title") String orderBy,
      @RequestParam(value = "direction", defaultValue = "DESC") Direction direction,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "after", required = false) Instant after,
      @RequestParam(value = "limit", defaultValue = "50") int limit
  ) {
    return ResponseEntity.ok(
        bookService.searchBooks(keyword, orderBy, direction, cursor, after, limit));
  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BookDto> createBook(
      @Valid @RequestPart("bookData") BookCreateRequest bookData,
      @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
  ) {
    //     Integer reviewCount, Double rating 관련 로직 필요 -> TODO
    BookDto createBook = bookService.registerBook(bookData, Optional.ofNullable(thumbnailImage));

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createBook);
  }

  @PostMapping(value = "/isbn/ocr", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<String> extractIsbnOcr(
      @RequestPart(value = "image") MultipartFile image
  ) {
    String isbn = bookService.extractIsbnFromImage(image);

    return ResponseEntity.ok(isbn);
  }

  @GetMapping("/info")
  public ResponseEntity<NaverBookDto> getBookByIsbn(
      @RequestParam("isbn") String isbn
  ) {
    return ResponseEntity.ok(bookService.getBookByIsbn(isbn));
  }

  @PatchMapping(value = "/{bookId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BookDto> updateBook(
      @PathVariable("bookId") UUID bookId,
      @RequestPart("bookData") BookUpdateRequest bookData,
      @RequestParam(value = "thumbnailImage", required = false) MultipartFile thumbnailImage
  ) {
    BookDto updateBook = bookService.updateBook(bookId, bookData,
        Optional.ofNullable(thumbnailImage));

    return ResponseEntity.ok(updateBook);
  }

  @DeleteMapping(value = "/{bookId}")
  public ResponseEntity<Void> deleteBookLogically(
      @PathVariable("bookId") UUID bookId
  ) {
    bookService.deleteBookLogically(bookId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @DeleteMapping(value = "/{bookId}/hard")
  public ResponseEntity<Void> deleteBookPhysically(
      @PathVariable("bookId") UUID bookId
  ) {
    bookService.deleteBookPhysically(bookId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  @GetMapping(value = "/popular")
  public ResponseEntity<CursorPageResponsePopularBookDto> getPopularBooks(
      @RequestParam(value = "period", defaultValue = "DAILY") PeriodType period,
      @RequestParam(value = "direction", defaultValue = "ASC") Direction direction,
      @RequestParam(value = "cursor", required = false) String cursor,
      @RequestParam(value = "after", required = false) Instant after,
      @RequestParam(value = "limit", defaultValue = "50") int limit
  ) {

    return ResponseEntity.ok(
        bookService.searchPopularBooks(period, direction, cursor, after, limit));
  }

  @GetMapping(value = "/{bookId}")
  public ResponseEntity<BookDto> getBookById(
      @PathVariable(name = "bookId", required = true) UUID bookId
  ) {

    BookDto findBook = bookService.getBookById(bookId);

    return ResponseEntity.ok(findBook);
  }
}
