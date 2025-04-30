package com.codeit.duckhu.domain.notification.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.domain.notification.repository.NotificationRepository;
import java.time.Instant;
import javax.sql.DataSource;

import io.micrometer.core.instrument.Counter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParametersBuilder;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.actuate.autoconfigure.metrics.task.TaskExecutorMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Import(NotificationBatchConfig.class)  // 실제 배치 설정을 테스트 컨텍스트에 로드
@ImportAutoConfiguration(exclude = TaskExecutorMetricsAutoConfiguration.class)  // 특정 auto-config 제외
@SpringBootTest(
    properties = {
        // 배치 잡 자동 실행 방지
        "spring.batch.job.enabled=false",
        // 테스트에서 빈 덮어쓰기 허용
        "spring.main.allow-bean-definition-overriding=true",
        // Task executor metrics 바인더 비활성화
        "management.metrics.binders.tasks.enabled=false",
        // H2 인메모리 DB 설정
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver"
    }
)
@AutoConfigureTestDatabase(replace = Replace.ANY)  // 모든 DataSource를 인메모리 DB로 교체
@SpringBatchTest  // Spring Batch 테스트 지원 활성화
@EnableAutoConfiguration  // 자동 설정 활성화(테스트 유틸 빈 등록 위해)
class NotificationBatchConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;  // 스텝/잡 실행 도구

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;  // 잡 메타데이터 관리 도구

    @Autowired
    @Qualifier("deleteNotificationsJob")  // deleteNotificationsJob 빈 주입
    private Job job;

    @MockitoBean
    private NotificationRepository notificationRepository;  // 레포지토리 mock

    @Autowired
    private Counter deleteSuccessCounter;  // 성공 카운터 빈 주입

    @Autowired
    private Counter deleteFailureCounter;  // 실패 카운터 빈 주입

    @TestConfiguration
    @EnableBatchProcessing  // 배치 처리 관련 빈(잡, 스텝 빌더 등) 활성화
    static class TestConfig {
        // --- 테스트용 Counter mock 빈 등록 ---
        @Bean
        public Counter deleteSuccessCounter() {
            // production에서 정의된 deleteSuccessCounter()를 mock으로 대체
            return mock(Counter.class);
        }
        @Bean
        public Counter deleteFailureCounter() {
            // production에서 정의된 deleteFailureCounter()를 mock으로 대체
            return mock(Counter.class);
        }

        // --- 방법 B: 스키마 스크립트 직접 로딩 ---
        @Bean
        public DataSourceInitializer batchSchemaInitializer(DataSource dataSource) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            // Spring Batch가 제공하는 H2 스키마 SQL 추가
            populator.addScript(
                new ClassPathResource("org/springframework/batch/core/schema-h2.sql")
            );
            DataSourceInitializer init = new DataSourceInitializer();
            init.setDataSource(dataSource);
            init.setDatabasePopulator(populator);
            return init;
        }
    }

    @BeforeEach
    void setUp() {
        // 기존 실행 내역 제거 (batch_job_instance 테이블이 있어야 성공)
        jobRepositoryTestUtils.removeJobExecutions();
        // jobLauncherTestUtils에 실제 실행할 Job 주입
        jobLauncherTestUtils.setJob(job);
    }

    @Test
    void deleteNotificationsStep_shouldCompleteAndCallRepository() throws Exception {
        // --- Step 실행 ---
        JobExecution exec = jobLauncherTestUtils.launchStep(
            "deleteNotificationsStep",
            new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters()
        );

        // --- 검증: Step이 완료 상태여야 함 ---
        assertEquals(BatchStatus.COMPLETED, exec.getStatus());

        // --- 검증: 레포지토리 메서드 호출 여부 ---
        verify(notificationRepository).deleteOldConfirmedNotifications(any(Instant.class));

        // --- 검증: 성공 카운터가 1회 increment 되었는지 ---
        verify(deleteSuccessCounter).increment();
    }
}