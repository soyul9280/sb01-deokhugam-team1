package com.codeit.duckhu.domain.review.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class PopularReviewJobConfig {

  private final JobRepository jobRepository;
  private final PopularReviewStepConfig stepConfig;

  @Bean
  public Job popularReviewJob() {
    return new JobBuilder("popularReviewJob", jobRepository)
        .start(stepConfig.popularReviewStep())
        .next(stepConfig.rankUpdateStep())
        .build();
  }
}
