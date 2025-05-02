package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.global.type.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("PopularBookBatchScheduler 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PopularBookBatchSchedulerTest {

  @Mock
  private PopularBookBatchService popularBookBatchService;

  @InjectMocks
  private PopularBookBatchScheduler scheduler;

  @Test
  @DisplayName("schedule(): 각 기간에 대해 savePopularBook이 정확히 1번씩 호출된다")
  void schedule_shouldCallSavePopularBookForAllPeriods() {
    // when
    scheduler.schedule();

    // then
    verify(popularBookBatchService).savePopularBook(PeriodType.DAILY);
    verify(popularBookBatchService).savePopularBook(PeriodType.WEEKLY);
    verify(popularBookBatchService).savePopularBook(PeriodType.MONTHLY);
    verify(popularBookBatchService).savePopularBook(PeriodType.ALL_TIME);

    // 총 4번 호출되었는지 확인
    verify(popularBookBatchService, times(4)).savePopularBook(any());
  }

  @Test
  @DisplayName("예외 발생 시에도 각 배치 작업이 독립적으로 실행된다")
  void schedule_shouldContinueEvenIfSomePeriodFails() {
    // given
    doThrow(new RuntimeException("예외 발생"))
        .when(popularBookBatchService)
        .savePopularBook(PeriodType.WEEKLY);

    // when
    scheduler.schedule();

    // then: 나머지 호출은 정상적으로 진행되어야 함
    verify(popularBookBatchService).savePopularBook(PeriodType.DAILY);
    verify(popularBookBatchService).savePopularBook(PeriodType.WEEKLY);
    verify(popularBookBatchService).savePopularBook(PeriodType.MONTHLY);
    verify(popularBookBatchService).savePopularBook(PeriodType.ALL_TIME);
  }
}