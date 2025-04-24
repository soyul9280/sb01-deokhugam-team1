package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.global.type.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PopularBookBatchScheduler {

  private final PopularBookBatchService popularBookBatchService;

  @Scheduled(cron = "0 0 12 * * *")
  public void schedule() {
    log.info("[일간 배치 작업] 일간 인기 도서를 갱신합니다");
    popularBookBatchService.savePopularBook(PeriodType.DAILY);

    log.info("[주간 배치 작업] 주간 인기 도서를 갱신합니다");
    popularBookBatchService.savePopularBook(PeriodType.WEEKLY);

    log.info("[월간 배치 작업] 월간 인기 도서를 갱신합니다");
    popularBookBatchService.savePopularBook(PeriodType.MONTHLY);

    log.info("[역대 배치 작업] 역대 인기 도서를 갱신합니다");
    popularBookBatchService.savePopularBook(PeriodType.ALL_TIME);

  }
}
