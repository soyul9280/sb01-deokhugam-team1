package com.codeit.duckhu.domain.review.repository;

import com.codeit.duckhu.domain.review.entity.QReview;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

// N + 1 문제를 해결하기 위해 작성하였습니다.
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  private final QReview review = QReview.review;

  /**
   * 도서 ID 목록에 해당하는 리뷰 수를 한 번에 조회합니다.
   * 논리 삭제되지 않은 리뷰만 대상으로 합니다.
   * @param bookIds
   * @return Map<bookId, 리뷰수>
   */
  @Override
  public Map<UUID, Integer> countByBookIds(List<UUID> bookIds) {
    return queryFactory
        .select(review.book.id, review.count())
        .from(review)
        .where(
            review.book.id.in(bookIds),
            review.isDeleted.isFalse()
        )
        .groupBy(review.book.id)
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(review.book.id),
            tuple -> Math.toIntExact(tuple.get(review.count()))
        ));
  }

  /**
   * 도서 ID 목록에 해당하는 평균 평점을 한 번에 조회합니다.
   * 논리 삭제되지 않은 리뷰만 대상으로 합니다.
   * @param bookIds
   * @return Map<bookId, 평균 평점>
   */
  @Override
  public Map<UUID, Double> averageRatingByBookIds(List<UUID> bookIds) {
    return queryFactory
        .select(review.book.id, review.rating.avg())
        .from(review)
        .where(
            review.book.id.in(bookIds),
            review.isDeleted.isFalse()
        )
        .groupBy(review.book.id)
        .fetch()
        .stream()
        .collect(Collectors.toMap(
            tuple -> tuple.get(review.book.id),
            tuple -> Optional.ofNullable(tuple.get(review.rating.avg())).orElse(0.0)
        ));
  }
}
