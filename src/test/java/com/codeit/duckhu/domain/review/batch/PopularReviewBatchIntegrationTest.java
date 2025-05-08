package com.codeit.duckhu.domain.review.batch;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.PopularReviewRepository;
import com.codeit.duckhu.global.type.PeriodType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.transaction.PlatformTransactionManager;

class PopularReviewBatchIntegrationTest {

  @Mock private PopularReviewRepository popularReviewRepository;
  @Mock private Review review;
  @Mock private PopularReview popularReview;
  @Mock private JobRepository jobRepository;
  @Mock private PlatformTransactionManager transactionManager;
  @Mock private EntityManagerFactory emf;
  @Mock private EntityManager em;
  @Mock private Query query;
  @Mock private EntityTransaction transaction;
  @Mock private StepExecution stepExecution;
  @Mock private StepContribution stepContribution;
  @Mock private ChunkContext chunkContext;
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    
    // Mock 객체 설정
    when(review.getLikeCount()).thenReturn(10);
    when(review.getCommentCount()).thenReturn(5);
    when(review.getRating()).thenReturn(4);
    when(emf.createEntityManager()).thenReturn(em);
    when(em.createQuery(anyString())).thenReturn(query);
    when(em.getTransaction()).thenReturn(transaction);
    when(query.setParameter(anyString(), any())).thenReturn(query);
    when(query.setFirstResult(anyInt())).thenReturn(query);
    when(query.setMaxResults(anyInt())).thenReturn(query);
    when(query.getResultList()).thenReturn(Collections.emptyList());
  }
  
  @Test
  @DisplayName("PopularReviewItemProcessor 테스트")
  void testPopularReviewItemProcessor() throws Exception {
    // given
    PopularReviewItemProcessor processor = new PopularReviewItemProcessor();
    java.lang.reflect.Field field = processor.getClass().getDeclaredField("periodParam");
    field.setAccessible(true);
    field.set(processor, "DAILY");
    
    // when
    processor.process(review);
    
    // then - 예외가 발생하지 않으면 성공
  }
  
  @Test
  @DisplayName("RankUpdateItemProcessor 테스트")
  void testRankUpdateItemProcessor() {
    // given
    RankUpdateItemProcessor processor = new RankUpdateItemProcessor("TEST");
    when(popularReview.getScore()).thenReturn(10.0);
    when(popularReview.getReview()).thenReturn(review);
    
    // when
    processor.process(popularReview);
    
    // then - 예외가 발생하지 않으면 성공
    verify(popularReview).setRank(1);
  }
  
  @Test
  @DisplayName("RankUpdateItemProcessor 스코어 0 필터링 테스트")
  void testRankUpdateItemProcessorFilterZeroScore() {
    // given
    RankUpdateItemProcessor processor = new RankUpdateItemProcessor("TEST");
    when(popularReview.getScore()).thenReturn(0.0);
    when(popularReview.getReview()).thenReturn(review);
    
    // when
    PopularReview result = processor.process(popularReview);
    
    // then
    assertNull(result); // 스코어가 0인 경우 null 반환
  }
  
  @Test
  @DisplayName("PopularReviewItemWriter 테스트")
  void testPopularReviewItemWriter() throws Exception {
    // given
    PopularReviewItemWriter writer = new PopularReviewItemWriter(popularReviewRepository);
    List<PopularReview> items = Collections.singletonList(popularReview);
    Chunk<PopularReview> chunk = new Chunk<>(items);
    
    // when
    writer.write(chunk);
    
    // then
    verify(popularReviewRepository).saveAll(items);
  }
  
  @Test
  @DisplayName("RankUpdateItemWriter 테스트")
  void testRankUpdateItemWriter() throws Exception {
    // given
    RankUpdateItemWriter writer = new RankUpdateItemWriter(popularReviewRepository);
    List<PopularReview> items = Collections.singletonList(popularReview);
    Chunk<PopularReview> chunk = new Chunk<>(items);
    
    // when
    writer.write(chunk);
    
    // then
    verify(popularReviewRepository).saveAll(items);
  }
  
  @Test
  @DisplayName("RankUpdateItemReader 초기화 테스트")
  void testRankUpdateItemReader() throws Exception {
    // Mock EntityManagerFactory 사용
    EntityManagerFactory mockEmf = mock(EntityManagerFactory.class);
    
    // RankUpdateItemReader 생성
    RankUpdateItemReader reader = new RankUpdateItemReader(mockEmf);
    
    // periodParam 설정
    reader.setPeriodParam("DAILY");
    
    // 예외가 발생하지 않으면 성공
  }

  @Test
  @DisplayName("RankUpdateItemReader 필드 테스트")
  void testRankUpdateItemReaderFields() throws Exception {
    // given
    RankUpdateItemReader reader = new RankUpdateItemReader(emf);

    // when/then - 필드 설정만 테스트
    reader.setPeriodParam("DAILY");
    reader.setParameterValues(Map.of("period", PeriodType.DAILY));

    java.lang.reflect.Field queryField = reader.getClass().getSuperclass().getDeclaredField("queryString");
    queryField.setAccessible(true);
    String queryString = (String) queryField.get(reader);
    assertNull(queryString);
  }
  
  @Test
  @DisplayName("StepTimingListener 테스트")
  void testStepTimingListener() {
    // given
    StepTimingListener listener = new StepTimingListener();
    when(stepExecution.getExitStatus()).thenReturn(ExitStatus.COMPLETED);
    
    // when
    listener.beforeStep(stepExecution);
    listener.afterStep(stepExecution);
    
    // then - 예외가 발생하지 않으면 성공
  }
  
  @Test
  @DisplayName("PopularReviewJobConfig 테스트")
  void testPopularReviewJobConfig() {
    // given
    PopularReviewJobConfig jobConfig = new PopularReviewJobConfig(jobRepository, mock(Step.class), mock(Step.class));
    
    // when
    Job job = jobConfig.popularReviewJob();
    
    // then
    assertNotNull(job);
  }
  
  @Test
  @DisplayName("PopularReviewStepConfig 일부 메소드 테스트")
  void testPopularReviewStepConfig() {
    // given
    PopularReviewStepConfig stepConfig = new PopularReviewStepConfig(
        jobRepository, 
        transactionManager, 
        emf,
        new PopularReviewItemProcessor(),
        new PopularReviewItemWriter(popularReviewRepository),
        popularReviewRepository
    );
    
    // when - 더 많은 메소드 호출 추가
    JpaPagingItemReader<Review> reader = stepConfig.popularReviewItemReader();
    
    // then
    assertNotNull(reader);
  }
  
  @Test
  @DisplayName("PopularReviewItemReader 테스트")
  void testPopularReviewItemReader() throws Exception {
    // given
    PopularReviewItemReader reader = new PopularReviewItemReader();
    
    // then - 생성만해도 성공
    assertNotNull(reader);
  }
  
  @Test
  @DisplayName("RankUpdateItemProcessor 순서 증가 테스트")
  void testRankUpdateItemProcessorIncrement() {
    // given
    RankUpdateItemProcessor processor = new RankUpdateItemProcessor("TEST");
    PopularReview review1 = mock(PopularReview.class);
    PopularReview review2 = mock(PopularReview.class);
    
    // review mock 설정
    when(review1.getScore()).thenReturn(10.0);
    when(review2.getScore()).thenReturn(8.0);
    when(review1.getReview()).thenReturn(review);
    when(review2.getReview()).thenReturn(review);
    
    // when - 여러 번 호출
    processor.process(review1);
    processor.process(review2);
    
    // then - rank 값이 설정되는지 확인
    verify(review1).setRank(1);
    verify(review2).setRank(2);
  }
  
  @Test
  @DisplayName("PopularReviewItemProcessor 다양한 기간 테스트")
  void testPopularReviewItemProcessorWithPeriods() throws Exception {
    for (String period : new String[]{"DAILY", "WEEKLY", "MONTHLY"}) {
      // given
      PopularReviewItemProcessor processor = new PopularReviewItemProcessor();
      java.lang.reflect.Field field = processor.getClass().getDeclaredField("periodParam");
      field.setAccessible(true);
      field.set(processor, period);
      
      // when
      processor.process(review);
      
      // then - 예외가 발생하지 않으면 성공
    }
  }
  
  private void assertNotNull(Object obj) {
    if (obj == null) {
      throw new AssertionError("객체가 null입니다");
    }
  }
}
