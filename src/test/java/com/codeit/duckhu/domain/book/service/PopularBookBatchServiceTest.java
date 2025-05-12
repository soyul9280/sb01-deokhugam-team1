package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.repository.popular.PopularBookRepository;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.global.type.PeriodType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PopularBookBatchService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class PopularBookBatchServiceTest {

  @Mock
  private BookRepository bookRepository;
  @Mock
  private ReviewRepository reviewRepository;
  @Mock
  private PopularBookRepository popularBookRepository;
  @Mock
  private MeterRegistry meterRegistry;
  @Mock
  private Counter mockCounter;

  @InjectMocks
  private PopularBookBatchService popularBookBatchService;

  @Test
  @DisplayName("savePopularBook(): 리뷰가 있는 도서를 기준으로 점수를 계산하고 인기 도서를 저장한다.")
  void savePopularBook_successfullyCalculatesAndSavesPopularBooks() {
    // given: 리뷰가 있는 도서를 반환하고, 해당 도서의 리뷰 수와 평점을 설정
    Book book = Book.builder().title("Test Book").build();
    List<Book> books = List.of(book);
    when(bookRepository.findBooksWithReviews()).thenReturn(books);
    when(reviewRepository.countByBookIdAndCreatedAtBetween(any(), any(), any())).thenReturn(10);
    when(reviewRepository.calculateAverageRatingByBookIdAndCreatedAtBetween(any(), any(), any()))
        .thenReturn(4.5);
    when(meterRegistry.counter(any(), any(), any())).thenReturn(mockCounter);

    // when: 일간 인기 도서 계산 배치 실행
    popularBookBatchService.savePopularBook(PeriodType.DAILY);

    // then: 각종 의존성 호출 및 저장 동작 검증
    verify(bookRepository).findBooksWithReviews();
    verify(reviewRepository).countByBookIdAndCreatedAtBetween(eq(book.getId()), any(), any());
    verify(reviewRepository).calculateAverageRatingByBookIdAndCreatedAtBetween(eq(book.getId()), any(), any());
    verify(popularBookRepository).deleteByPeriod(PeriodType.DAILY);
    verify(popularBookRepository).saveAll(anyList());
    verify(mockCounter).increment();
  }
}
