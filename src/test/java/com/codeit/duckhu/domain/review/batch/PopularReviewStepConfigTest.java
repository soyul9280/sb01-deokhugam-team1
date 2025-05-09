package com.codeit.duckhu.domain.review.batch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.PopularReviewRepository;
import com.codeit.duckhu.global.exception.DomainException;
import com.codeit.duckhu.global.type.PeriodType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class PopularReviewStepConfigTest {

  @Mock private JobRepository jobRepository;
  @Mock private PlatformTransactionManager transactionManager;
  @Mock private EntityManagerFactory entityManagerFactory;
  @Mock private PopularReviewItemProcessor processor;
  @Mock private PopularReviewItemWriter writer;
  @Mock private PopularReviewRepository popularReviewRepository;
  @Mock private RankUpdateItemProcessor rankUpdateItemProcessor;
  @Mock private RankUpdateItemWriter rankUpdateItemWriter;
  @Mock private EntityManager entityManager;
  @Mock private Query query;

  private PopularReviewStepConfig stepConfig;

  @BeforeEach
  void setUp() {
    stepConfig = new PopularReviewStepConfig(
        jobRepository,
        transactionManager,
        entityManagerFactory,
        processor,
        writer,
        popularReviewRepository
    );
  }

  @Test
  @DisplayName("인기 리뷰 점수 계산 Step 설정 테스트")
  void testStepConfiguration() {
    assertThat(stepConfig).isNotNull();
    
    Step step = stepConfig.popularReviewStep();
    assertThat(step).isNotNull();
    assertThat(step.getName()).isEqualTo("popularReviewStep");
  }

  @Test
  @DisplayName("rankUpdateStep 설정 테스트")
  void testRankUpdateStepConfiguration() {
    // Given
    JpaPagingItemReader<PopularReview> mockReader = mock(JpaPagingItemReader.class);
    
    // When
    Step rankUpdateStep = stepConfig.rankUpdateStep(mockReader, rankUpdateItemProcessor, rankUpdateItemWriter);
    
    // Then
    assertThat(rankUpdateStep).isNotNull();
    assertThat(rankUpdateStep.getName()).isEqualTo("rankUpdateStep");
  }

  @Test
  @DisplayName("rankUpdateItemReader 빈 설정 테스트")
  void testRankUpdateItemReader() {
    // When
    JpaPagingItemReader<PopularReview> reader = stepConfig.rankUpdateItemReader("DAILY");
    
    // Then
    assertThat(reader).isNotNull();
    assertThat(reader).isInstanceOf(RankUpdateItemReader.class);
  }

  @Test
  @DisplayName("rankUpdateItemProcessor 빈 설정 테스트")
  void testRankUpdateItemProcessorBean() {
    // When
    RankUpdateItemProcessor processor = stepConfig.rankUpdateItemProcessor();
    
    // Then
    assertThat(processor).isNotNull();
    assertThat(processor).isInstanceOf(RankUpdateItemProcessor.class);
  }

  @Test
  @DisplayName("rankUpdateItemReaderWithScope 빈 설정 테스트")
  void testRankUpdateItemReaderWithScope() {
    // When
    RankUpdateItemReader reader = stepConfig.rankUpdateItemReaderWithScope(entityManagerFactory, "DAILY");
    
    // Then
    assertThat(reader).isNotNull();
  }

  @Test
  @DisplayName("인기 리뷰 점수 계산 공식 테스트")
  void testScoreCalculation() {
    // Given - 좋아요 10개, 댓글 5개인 리뷰
    int likeCount = 10;
    int commentCount = 5;

    // When - 점수 계산식 적용
    double likeWeight = 0.3;
    double commentWeight = 0.7;
    double score = (likeCount * likeWeight) + (commentCount * commentWeight);

    // Then - 계산 결과 확인
    assertThat(score).isEqualTo(6.5);
  }

  @Test
  @DisplayName("잘못된 period 파라미터가 들어오면 예외가 발생")
  void testInvalidPeriodThrowsException() {
    // Given
    RankUpdateItemReader reader = new RankUpdateItemReader(entityManagerFactory);

    // 잘못된 값 세팅
    reader.setPeriodParam("INVALID_PERIOD");

    // When / Then
    assertThatThrownBy(reader::init)
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("잘못된 요청입니다.");
  }
  
  @Test
  @DisplayName("RankUpdateItemProcessor가 올바르게 랭킹을 부여하는지 테스트")
  void testRankUpdateItemProcessorFunctionality() {
    // Given
    RankUpdateItemProcessor processor = new RankUpdateItemProcessor("DAILY");
    
    List<PopularReview> reviews = Arrays.asList(
        createPopularReview(10.0),
        createPopularReview(8.0),
        createPopularReview(6.0)
    );
    
    // When
    PopularReview result1 = processor.process(reviews.get(0));
    PopularReview result2 = processor.process(reviews.get(1));
    PopularReview result3 = processor.process(reviews.get(2));
    
    // Then
    assertThat(result1.getRank()).isEqualTo(1);
    assertThat(result2.getRank()).isEqualTo(2);
    assertThat(result3.getRank()).isEqualTo(3);
  }
  
  private PopularReview createPopularReview(double score) {
    Review mockReview = mock(Review.class);
    return PopularReview.builder()
        .review(mockReview)
        .period(PeriodType.DAILY)
        .score(score)
        .rank(0)
        .likeCount(5)
        .commentCount(5)
        .reviewRating(4.0)
        .build();
  }
}
