package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl {
  private final BookRepository bookRepository;
}
