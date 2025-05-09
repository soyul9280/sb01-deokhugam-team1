package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.global.type.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularReviewScheduledRunner {

  private final JobLauncher jobLauncher;
  private final Job popularReviewJob;

  @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
  public void runPopularReviewJobForAllPeriods() {
    long totalStart = System.currentTimeMillis();
    log.info("전체 배치 시작");

    for (PeriodType period : PeriodType.values()) {
      try {
        long start = System.currentTimeMillis();

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("period", period.name())
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        jobLauncher.run(popularReviewJob, jobParameters);

        long duration = System.currentTimeMillis() - start;
        log.info("✅ [{}] 배치 완료: {}ms", period.name(), duration);
      } catch (Exception e) {
        log.debug("[{}] 배치 실패", period.name(), e);
      }
    }

    long totalDuration = System.currentTimeMillis() - totalStart;
    log.info("✅ 전체 배치 종료: 총 {}ms 소요", totalDuration);
  }
}