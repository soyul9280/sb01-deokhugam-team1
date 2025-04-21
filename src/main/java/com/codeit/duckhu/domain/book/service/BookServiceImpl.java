package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.dto.BookUpdateRequest;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.exception.BookException;
import com.codeit.duckhu.domain.book.exception.OCRException;
import com.codeit.duckhu.domain.book.naver.NaverBookClient;
import com.codeit.duckhu.domain.book.dto.BookCreateRequest;
import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.dto.CursorPageResponseBookDto;
import com.codeit.duckhu.domain.book.dto.NaverBookDto;
import com.codeit.duckhu.domain.book.mapper.BookMapper;
import com.codeit.duckhu.domain.book.ocr.OcrExtractor;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.storage.ThumbnailImageStorage;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.global.exception.ErrorCode;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
주석 처리한 부분은 차후 구현할 내용들
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  private final ReviewRepository reviewRepository;

  private final BookMapper bookMapper;

  private final ThumbnailImageStorage thumbnailImageStorage;

  private final NaverBookClient naverBookClient;

  private final OcrExtractor ocrExtractor;
  /**
   * 도서 등록
   * @param bookData 도서 생성 요청 DTO
   * @param thumbnailImage 썸네일 이미지 : 선택적
   * @return 도서 DTO
   */
  @Override
  @Transactional
  public BookDto registerBook(BookCreateRequest bookData, Optional<MultipartFile> thumbnailImage) {
    String isbn = bookData.isbn();
    if (isbn != null) {
      //isbn 형식 검사
      if (!validIsbn(isbn)) {
        log.info("[도서 등록 실패] 잘못된 ISBN 형식 : {}", isbn);
        throw new BookException(ErrorCode.INVALID_ISBN_FORMAT);
      }

      // isbn은 중복될 수 없다.
      if (bookRepository.existsByIsbn(isbn)) {
        log.info("[도서 등록 실패] 중복된 ISBN: {}", isbn);
        throw new BookException(ErrorCode.DUPLICATE_ISBN);
      }
    }

    // 썸네일 S3 업로드
    String thumbnailUrl = thumbnailImage
        .filter(file -> !file.isEmpty())
        .map(thumbnailImageStorage::upload)
        .orElse(null);
    log.info("[이미지 업로드] S3에 업로드 완료: {}", thumbnailUrl);

    Book book = Book.builder()
        .title(bookData.title())
        .author(bookData.author())
        .description(bookData.description())
        .publisher(bookData.publisher())
        .publishedDate(bookData.publishedDate())
        .isbn(bookData.isbn())
        .thumbnailUrl(thumbnailUrl)
        .isDeleted(false)
        .build();

    bookRepository.save(book);
    log.info("[도서 등록 완료] id: {}, isbn: {}", book.getId(), book.getIsbn());

    // 도서 등록 시 초기 리뷰수와 평점은 0이다.
    return bookMapper.toDto(book, 0, 0.0);
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

  @Override
  public BookDto updateBook(UUID id, BookUpdateRequest bookUpdateRequest,
      Optional<MultipartFile> thumbnailImage) {
    // 도서 존재 여부 확인
    Book book = bookRepository.findById(id)
        .filter(b -> !b.getIsDeleted())
        .orElseThrow(() -> {
          log.info("[도서 수정 실패] 존재하지 않거나 삭제된 도서입니다. ID : {}", id);
          return new BookException(ErrorCode.BOOK_NOT_FOUND);
        });

    // 썸네일 이미지가 있다면 새로 업로드 후 갱신
    thumbnailImage.filter(file -> !file.isEmpty())
        .ifPresent(file -> {
          String uploadUrl = thumbnailImageStorage.upload(file);
          log.info("[도서 수정] 썸네일 이미지 변경됨: {}", uploadUrl);
          book.updateThumbnailUrl(uploadUrl);
        });

    // 일반적인 정보 업데이트
    book.updateInfo(
        bookUpdateRequest.title(),
        bookUpdateRequest.author(),
        bookUpdateRequest.description(),
        bookUpdateRequest.publisher(),
        bookUpdateRequest.publishedDate()
    );

    int reviewCount = reviewRepository.countByBookId(book.getId());
    double rating = reviewRepository.calculateAverageRatingByBookId(book.getId());

    log.info("[도서 수정 완료] ID : {}", id);
    return bookMapper.toDto(book, reviewCount, rating);
  }


  /**
   * 도서 Isbn을 입력하면 Naver API에서 해당하는 도서 정보를 받습니다
   * @param isbn 도서 Isbn
   * @return NaverBook Dto
   */
  @Override
  public NaverBookDto getBookByIsbn(String isbn) {
    if (!validIsbn(isbn)) {
      log.info("[도서 조회 실패] 잘못된 ISBN 형식 : {}", isbn);
      throw new BookException(ErrorCode.INVALID_ISBN_FORMAT);
    }

    return naverBookClient.searchByIsbn(isbn);
  }

  /**
   *  입력값으로 주어진 isbn을 검증합니다.
   * @param isbn isbn-13만 허용합니다.
   * @return
   */
  private boolean validIsbn(String isbn) {
    // 하이픈이나 공백을 제거
    String cleanIsbn = isbn.replaceAll("[-\\s]", "");

    //숫자가 13자리인지 확인
    if (!cleanIsbn.matches("\\d{13}")) {
      return false;
    }

    int sum = 0;
    for (int i = 0; i < 13; i++) {
      int digit = cleanIsbn.charAt(i) - '0';
      sum += (i % 2 == 0) ? digit : digit * 3;
    }

    return sum % 10 == 0;
  }

  @Override
  public String extractIsbnFromImage(MultipartFile image) {
    // 이미지 형식인지 검증
    String contentType = image.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new OCRException(ErrorCode.INVALID_IMAGE_FORMAT);
    }

    return ocrExtractor.extractOCR(image);
  }

  /**
   * 도서를 논리 삭제합니다. (리뷰와 댓글 유지)
   * @param id 도서 아이디
   */
  @Override
  public void deleteBookLogically(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookException(ErrorCode.BOOK_NOT_FOUND));

    book.logicallyDelete();
  }

  /**
   * 도서를 물리 삭제합니다. (리뷰와 댓글도 같이 삭제)
   * @param id 도서 아이디
   */
  @Override
  public void deleteBookPhysically(UUID id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookException(ErrorCode.BOOK_NOT_FOUND));

    bookRepository.delete(book);
  }
}
