package com.codeit.duckhu.domain.book.batch;


import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.repository.popular.PopularBookRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PopularBookBatchConfig {

    // JobBuilderFactory: Job() 인스턴스를 생성하는 팩토리
    private final JobRepository jobRepository;
    // Step 내 chunk 트랜잭션에 사용할 TransactionManager
    private final PlatformTransactionManager transactionManager;
    // JPA Reader에 주입할 EntityManagerFactory
    private final EntityManagerFactory emf;

    // 도메인 리포지토리
    private final BookRepository bookRepository;
    private final PopularBookRepository popularBookRepository;

    private final BookItemReader reader;
    private final BookScoreProcessor processor;
//   private final PopularBookWriter writer;

    /**
     * Job 정의 - 여러 Step을 순차적으로 또는 병렬로 실행할 수 있는 배치 단위
     */
    @Bean
    public Job popularBookJob(Step dailyStep, Step weeklyStep, Step monthlyStep, Step allTimeStep) {
        return new JobBuilder("popularBookJob", jobRepository)   // 2-arg 생성자 사용 :contentReference[oaicite:0]{index=0}
            .start(dailyStep)
            .next(weeklyStep)
            .next(monthlyStep)
            .next(allTimeStep)
            .build();
    }

    @Bean
    public Step dailyStep(
        ItemWriter<PopularBookScore> writer
    ) {
        return new StepBuilder("dailyPopularBookStep", jobRepository)// 2-arg 생성자 사용 :contentReference[oaicite:1]{index=1}
            .<Book, PopularBookScore>chunk(100, transactionManager)     // 청크 크기, 트랜잭션 매니저 지정
            .reader(reader)
            .processor(processor)
            .writer(writer)
//            .listener(new StepLoggingListener("DAILY"))
            .build();
    }

    @Bean
    public Step weeklyStep(
        ItemWriter<PopularBookScore> writer
    ) {
        return new StepBuilder("weeklyPopularBookStep", jobRepository)
            .<Book, PopularBookScore>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
//            .listener(new StepLoggingListener("WEEKLY"))
            .build();
    }

    @Bean
    public Step monthlyStep(
        ItemWriter<PopularBookScore> writer
    ) {
        return new StepBuilder("monthlyPopularBookStep", jobRepository)
            .<Book, PopularBookScore>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
//            .listener(new StepLoggingListener("MONTHLY"))
            .build();
    }

    @Bean
    public Step allTimeStep(
        ItemWriter<PopularBookScore> writer
    ) {
        return new StepBuilder("allTimePopularBookStep", jobRepository)
            .<Book, PopularBookScore>chunk(100, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
//            .listener(new StepLoggingListener("ALL_TIME"))
            .build();
    }
}

