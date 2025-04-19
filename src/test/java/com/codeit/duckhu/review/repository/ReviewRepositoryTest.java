package com.codeit.duckhu.review.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.codeit.duckhu.review.entity.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class ReviewRepositoryTest {

  @Autowired
  private ReviewRepository repository;

  @Test
  @DisplayName("리뷰 저장 확인")
  void saveReview_shouldSavedReview() {
    // Given
    Review savedReview = Review.builder()
        .rating(5)
        .content("재밌어요")
        .likeCount(0)
        .commentCount(0)
        .likeByMe(false)
        .build();

    // When
    Review foundReview = repository.save(savedReview);

    // Then
    assertThat(foundReview).isNotNull();
    assertThat(foundReview.getRating()).isEqualTo(5);
    assertThat(foundReview.getContent()).isEqualTo("재밌어요");
    assertThat(foundReview.getLikeCount()).isEqualTo(0);
    assertThat(foundReview.getCommentCount()).isEqualTo(0);
    assertThat(foundReview.getLikeByMe()).isEqualTo(false);
  }
}