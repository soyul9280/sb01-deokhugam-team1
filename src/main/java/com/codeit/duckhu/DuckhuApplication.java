package com.codeit.duckhu;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// Spring Batch 인프라(PlatformTransactionManager, JobRepository 등)를 자동 구성
@EnableBatchProcessing
public class DuckhuApplication {

  public static void main(String[] args) {
    SpringApplication.run(DuckhuApplication.class, args);
  }
}
