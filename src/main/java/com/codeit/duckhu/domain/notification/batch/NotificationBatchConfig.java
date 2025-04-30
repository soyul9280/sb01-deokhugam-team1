package com.codeit.duckhu.domain.notification.batch;

import com.codeit.duckhu.domain.notification.repository.NotificationRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NotificationBatchConfig {

    // JobBuilderFactory: Job() 인스턴스를 생성하는 팩토리
    private final JobRepository jobRepository;
    // Step 내 chunk 트랜잭션에 사용할 TransactionManager
    private final PlatformTransactionManager transactionManager;

    private final NotificationRepository notificationRepository;
    private final MeterRegistry meterRegistry;

    // 1) 미리 Counter 빈으로 등록
    @Bean
    public Counter deleteSuccessCounter() {
        return meterRegistry.counter("batch.notification.delete.success");
    }

    @Bean
    public Counter deleteFailureCounter() {
        return meterRegistry.counter("batch.notification.delete.failure");
    }

    // 2) Tasklet에 Counter 주입
    @Bean
    public Tasklet deleteConfirmedNotificationsTasklet(Counter deleteSuccessCounter, Counter deleteFailureCounter) {
        return (contribution, chunkContext) -> {
            Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);

            try {
                notificationRepository.deleteOldConfirmedNotifications(cutoff);
                deleteSuccessCounter.increment();
            } catch (Exception ex) {
                deleteFailureCounter.increment();
                throw ex;
            }
            return RepeatStatus.FINISHED;
        };
    }

    // 2) Step 정의: Tasklet을 하나의 Step으로 묶음
    @Bean
    public Step deleteNotificationsStep(Tasklet deleteConfirmedNotificationsTasklet) {
        return new StepBuilder("deleteNotificationsStep", jobRepository)
            .tasklet(deleteConfirmedNotificationsTasklet, transactionManager)
            .build();
    }

    // Job도 같은 방식으로 Step 파라미터 주입
    @Bean
    public Job deleteNotificationsJob(Step deleteNotificationsStep) {
        return new JobBuilder("deleteNotificationsJob", jobRepository)
            .start(deleteNotificationsStep)
            .build();
    }
}
