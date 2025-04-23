package com.codeit.duckhu.domain.review.repository.custom;


import com.codeit.duckhu.domain.review.entity.Review;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReviewRepositoryCustom {

  List<Review> findReviewsWithCursor(
      String keyword,
      String orderBy,
      String direction,
      UUID userId,
      UUID bookId,
      String cursor,
      Instant after,
      int size
  );

}
