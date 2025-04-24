package com.codeit.duckhu.domain.review.repository.custom.impl;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.repository.custom.PopularReviewRepositoryCustom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PopularReviewRepositoryImpl implements PopularReviewRepositoryCustom {

  @Override
  public List<PopularReview> findReviewsWithCursor(String keyword, String orderBy, String direction,
      UUID userId, UUID bookId, String cursor, Instant after, int size) {
    return List.of();
  }
}
