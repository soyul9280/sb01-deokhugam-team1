package com.codeit.duckhu.domain.book.service;

import static org.mockito.BDDMockito.*;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.repository.popular.PopularBookRepository;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.global.type.PeriodType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PopularBookBatchServiceTest {

  @Mock private BookRepository bookRepository;
  @Mock private ReviewRepository reviewRepository;
  @Mock private PopularBookRepository popularBookRepository;
  @Mock private MeterRegistry meterRegistry;

  @InjectMocks private PopularBookBatchService popularBookBatchService;

  @Test
  @DisplayName("인기 도서 배치 저장 성공")
  void savePopularBook_success() {
    // Given
    Book book = Book.builder()
        .title("테스트 도서")
        .publishedDate(LocalDate.now())
        .isDeleted(false)
        .build();
    ReflectionTestUtils.setField(book, "id", UUID.randomUUID());

    given(bookRepository.findBooksWithReviews()).willReturn(List.of(book));
    given(reviewRepository.countByBookIdAndCreatedAtBetween(any(), any(), any())).willReturn(5);
    given(reviewRepository.calculateAverageRatingByBookIdAndCreatedAtBetween(any(), any(), any())).willReturn(4.5);
    given(meterRegistry.counter(anyString(), anyString(), anyString())).willReturn(mock(Counter.class));

    // When
    popularBookBatchService.savePopularBook(PeriodType.DAILY);

    // Then
    verify(bookRepository).findBooksWithReviews();
    verify(reviewRepository).countByBookIdAndCreatedAtBetween(eq(book.getId()), any(), any());
    verify(reviewRepository).calculateAverageRatingByBookIdAndCreatedAtBetween(eq(book.getId()), any(), any());
    verify(popularBookRepository).deleteByPeriod(PeriodType.DAILY);
    verify(popularBookRepository).saveAll(anyList());
    verify(meterRegistry).counter("batch.book.popularBook.success", "period", "DAILY");
  }

  @Test
  @DisplayName("인기 도서 배치 저장 실패 시 실패 카운터 증가")
  void savePopularBook_fail() {
    // Given
    given(bookRepository.findBooksWithReviews()).willThrow(new RuntimeException("DB 에러"));
    given(meterRegistry.counter(anyString(), anyString(), anyString())).willReturn(mock(Counter.class));

    // When
    popularBookBatchService.savePopularBook(PeriodType.DAILY);

    // Then
    verify(meterRegistry).counter("batch.book.popularBook.failure", "period", "DAILY");
  }
}

