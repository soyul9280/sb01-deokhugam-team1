package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.BookUpdateRequest;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.dto.CursorPageResponsePopularBookDto;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.codeit.duckhu.domain.book.mapper.BookMapper;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
주석 처리한 부분은 차후 구현할 내용들
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

  private final BookRepository repository;

  private final BookMapper bookMapper;

  /**
   * 도서 등록
   * @param bookData 도서 생성 요청 DTO
   * @param thumbnailImage 썸네일 이미지 : 선택적
   * @return 도서 DTO
   */
  @Override
  public BookDto registerBook(BookCreateRequest bookData, Optional<MultipartFile> thumbnailImage) {
    return null;
  }

  /**
   * 도서 목록 조회
   * @param keyword 도서 제목, 저자, ISBN의 키워드를 통해 조회
   * @param orderBy 정렬 기준 title, publishedDate, rating, reviewCount
   * @param direction 정렬 방향 DESC (기본값), ASC
   * @param cursor 커서 페이지네이션 커서
   * @param after 보조 커서 (createdAt)
   * @param limit 페이지 크기 (50)
   * @return 도서 페이지 응답 DTO
   */
  @Override
  public CursorPageResponseBookDto searchBooks(String keyword, String orderBy, String direction,
      String cursor, Instant after, int limit) {
    return null;
  }

//  @Override
//  public CursorPageResponsePopularBookDto searchPopularBooks(PeriodType period, String direction, String cursor,
//      Instant after, int limit) {
//    return null;
//  }

  /**
   * 도서 정보 상세 조회
   * @param id 도서 ID
   * @return 도서 DTO
   */
  @Override
  public BookDto getBookById(UUID id) {
    return null;
  }

//  @Override
//  public BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest,
//      Optional<MultipartFile> thumbnailImage) {
//    return null;
//  }
//
//  @Override
//  public NaverBookDto getBookByIsbn(String isbn) {
//    return null;
//  }
//
  @Override
  public String extractIsbnFromImage(Optional<MultipartFile> image) {
    return "";
  }
//
//  @Override
//  public void deleteBookLogically(UUID id) {
//
//  }
//
//  @Override
//  public void deleteBookPhysically(UUID id) {
//
//  }
}
