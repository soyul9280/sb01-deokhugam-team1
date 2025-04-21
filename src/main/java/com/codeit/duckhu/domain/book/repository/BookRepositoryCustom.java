package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.book.dto.Cursor;
import com.codeit.duckhu.domain.book.entity.Book;
import java.util.List;

public interface BookRepositoryCustom {
  List<Book> searchBooks(String keyword, String sortBy, Cursor cursor, int limit, boolean isAsc);
}
