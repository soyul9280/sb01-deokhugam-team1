package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.book.entity.Book;
import java.time.Instant;
import java.util.List;

public interface BookRepositoryCustom {
  List<Book> searchBooks(
      String keyword, String orderBy, String direction, String cursor, Instant after, int limit);
}
