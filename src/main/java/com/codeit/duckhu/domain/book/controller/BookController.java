package com.codeit.duckhu.domain.book.controller;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.service.BookService;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  @GetMapping
  public ResponseEntity<CursorPageResponseBookDto> getBooks(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "title") String orderBy,
      @RequestParam(defaultValue = "DESC") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) Instant after,
      @RequestParam(defaultValue = "50") int limit
  ) {
    return ResponseEntity.ok(
        bookService.searchBooks(keyword, orderBy, direction, cursor, after, limit));
  }

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<BookDto> createBook(
      @RequestPart("bookData") BookCreateRequest bookData,
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
      @RequestPart(value = "image", required = false) MultipartFile image
  ) {
    String isbn = bookService.extractIsbnFromImage(image);
    return ResponseEntity.ok(isbn);
  }
}
