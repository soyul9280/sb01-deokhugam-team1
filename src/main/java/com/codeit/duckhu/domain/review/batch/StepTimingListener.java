package com.codeit.duckhu.domain.review.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class StepTimingListener implements StepExecutionListener {

  private long start;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    start = System.currentTimeMillis();
    log.info("Step 시작: {}", stepExecution.getStepName());
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    long duration = System.currentTimeMillis() - start;
    log.info("Step [{}] 소요 시간: {}ms", stepExecution.getStepName(), duration);
    return ExitStatus.COMPLETED;
  }
}