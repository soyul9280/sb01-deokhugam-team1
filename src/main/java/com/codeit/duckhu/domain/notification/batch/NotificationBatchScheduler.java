package com.codeit.duckhu.domain.notification.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteNotificationsJob;

    /**
     * 매일 00시 30분에 Spring Batch Job 실행
     */
    @Scheduled(cron = "0 30 0 * * *", zone = "Asia/Seoul")
    public void runBatchJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
            jobLauncher.run(deleteNotificationsJob, params);
        } catch (Exception e) {
            // 실패 카운터 증가 or 로그 처리
            log.error("deleteNotificationsJob failed", e);
        }
    }
}

