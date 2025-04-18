package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.BookUpdateRequest;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

  BookDto registerBook(BookCreateRequest bookData, Optional<MultipartFile> thumbnailImage);

  CursorPageResponseBookDto searchBooks(String keyword, String orderBy, String direction,
      String cursor, Instant after, Integer limit);

  CursorPageResponseBookDto searchBooks(PeriodType period, String direction, String cursor,
      Instant after, Integer limit);

  BookDto getBookById(UUID id);

  BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest, Optional<MultipartFile> thumbnailImage);

  NaverBookDto getBookByIsbn(String isbn);

  String extractIsbnFromImage(Optional<MultipartFile> image);

  void deleteBookLogically(UUID id);

  void deleteBookPhysically(UUID id);
}
