package com.codeit.duckhu.domain.user.repository.poweruser;

import com.codeit.duckhu.domain.comment.domain.QComment;
import com.codeit.duckhu.domain.review.entity.QLikedUserId;
import com.codeit.duckhu.domain.review.entity.QReview;
import com.codeit.duckhu.domain.user.dto.PowerUserStatsDto;
import com.codeit.duckhu.domain.user.entity.PowerUser;
import com.codeit.duckhu.domain.user.entity.QPowerUser;
import com.codeit.duckhu.domain.user.entity.QUser;
import com.codeit.duckhu.global.type.Direction;
import com.codeit.duckhu.global.type.PeriodType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class PowerUserRepositoryImpl implements PowerUserRepositoryCustom {
  private final JPAQueryFactory queryFactory;
  QPowerUser powerUser = QPowerUser.powerUser;
  QUser user = QUser.user;
  QReview review = QReview.review;
  QComment comment = QComment.comment;
  QLikedUserId likedUser = QLikedUserId.likedUserId;

  @Override
  public List<PowerUserStatsDto> findPowerUserStatsBetween(Instant start, Instant end) {
    // 1. 리뷰 인기 점수 총합 (작성자 기준으로 group by)
    List<Tuple> reviewScores = queryFactory
            .select(
                    review.user.id,
                    review.likeCount.multiply(0.3).add(review.commentCount.multiply(0.7)).sum()
            )
            .from(review)
            .where(
                    review.createdAt.between(start, end),
                    review.isDeleted.eq(false)
            )
            .groupBy(review.user.id)
            .fetch();

    //유저가 누른 좋아요 수
    Map<UUID, Long> likedCounts = queryFactory
            .select(likedUser.userId, likedUser.count())
            .from(likedUser)
            .where(likedUser.createdAt.between(start, end))
            .groupBy(likedUser.userId)
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                    t -> t.get(likedUser.userId),
                    t -> t.get(1, Long.class)
            ));

    //유저가 쓴 댓글 수
    Map<UUID,Long> commentCounts=queryFactory
            .select(comment.user.id,comment.count())
            .from(comment)
            .where(comment.createdAt.between(start,end),
                    comment.isDeleted.eq(false))
            .groupBy(comment.user.id)
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                    t->t.get(comment.user.id),
                    t->t.get(1, Long.class)
            ));

    return reviewScores.stream()
            .map(tuple -> {
              UUID userId = tuple.get(review.user.id);
              Double reviewScoreSum = tuple.get(1, Double.class);

              Long likeCount = likedCounts.getOrDefault(userId, 0L);
              Long commCount = commentCounts.getOrDefault(userId, 0L);

              return PowerUserStatsDto.builder()
                      .userId(userId)
                      .reviewScoreSum(reviewScoreSum)
                      .likedCount(likeCount)
                      .commentCount(commCount)
                      .build();
            }).toList();
  }

  @Override
  public List<PowerUser> searchByPeriodWithCursorPaging(
      PeriodType period, Direction direction,
      String cursor,
      Instant after,
      int limit) {
    BooleanBuilder condition = new BooleanBuilder();
    condition.and(powerUser.period.eq(period));
    condition.and(user.deleted.eq(false));

    if(cursor != null && after != null) {
      condition.and(getCursorCondition(cursor, after, isAsc(direction)));
    }
    List<OrderSpecifier<?>> orders = getOrderSpecifiers(isAsc(direction));

    return queryFactory
            .selectFrom(powerUser)
            .join(powerUser.user, user).fetchJoin()
            .where(condition)
            .orderBy(orders.toArray(OrderSpecifier[]::new))
            .limit(limit)
            .fetch();
  }

  private boolean isAsc(Direction direction) {
    return direction == Direction.ASC;
  }

  private List<OrderSpecifier<?>> getOrderSpecifiers(boolean asc) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();
    orders.add(asc ? powerUser.score.asc() : powerUser.score.desc());
    orders.add(asc ? powerUser.createdAt.asc() : powerUser.createdAt.desc());
    orders.add(asc ? powerUser.user.id.asc() : powerUser.user.id.desc());
    return orders;
  }

  private BooleanBuilder getCursorCondition(
          String cursor, Instant after, boolean isAsc) {
    UUID cursorId = UUID.fromString(cursor);
    BooleanBuilder builder = new BooleanBuilder();

    if (isAsc) {
      builder.and(powerUser.createdAt.gt(after)
                      .or(powerUser.createdAt.eq(after)
                              .and(powerUser.user.id.gt(cursorId))));
    }else{
      builder.and(powerUser.createdAt.lt(after)
              .or(powerUser.createdAt.eq(after)
                      .and(powerUser.user.id.lt(cursorId))));
    }

    return builder;
  }

}
