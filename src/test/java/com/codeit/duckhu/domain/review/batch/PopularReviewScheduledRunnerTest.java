package com.codeit.duckhu.domain.review.batch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.global.type.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

@ExtendWith(MockitoExtension.class)
class PopularReviewScheduledRunnerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job popularReviewJob;

  @InjectMocks
  private PopularReviewScheduledRunner scheduledRunner;

  @Test
  @DisplayName("모든 기간에 대해 인기 리뷰 배치 작업이 실행되는지 확인")
  void runPopularReviewJobForAllPeriods_shouldRunBatchForAllPeriods() throws Exception {
    // When
    scheduledRunner.runPopularReviewJobForAllPeriods();

    // Then
    verify(jobLauncher, times(PeriodType.values().length))
        .run(eq(popularReviewJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("배치 작업 중 예외 발생 시 다른 기간 작업은 계속 실행되는지 확인")
  void runPopularReviewJobForAllPeriods_shouldContinueOnException() throws Exception {
    // Given
    doThrow(new RuntimeException("배치 작업 실패"))
        .when(jobLauncher).run(eq(popularReviewJob), argThat(params -> 
            PeriodType.DAILY.name().equals(params.getString("period"))));

    // When
    scheduledRunner.runPopularReviewJobForAllPeriods();

    // Then
    verify(jobLauncher, times(PeriodType.values().length))
        .run(eq(popularReviewJob), any(JobParameters.class));
  }
}