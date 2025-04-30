package com.codeit.duckhu.domain.review.batch;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.global.type.PeriodType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PopularReviewItemProcessorTest {

  private PopularReviewItemProcessor processor;

  @Test
  @DisplayName("리뷰를 인기 리뷰로 변환 성공")
  void process_shouldConvertReviewToPopularReview() throws Exception {
    // Given
    processor = new PopularReviewItemProcessor(); // 빈 생성자
    ReflectionTestUtils.setField(processor, "periodParam", "DAILY"); // 필드 주입

    Review review = Review.builder().rating(5).likeCount(10).commentCount(5).build();

    // When
    PopularReview result = processor.process(review);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getPeriod()).isEqualTo(PeriodType.DAILY);
  }
}
