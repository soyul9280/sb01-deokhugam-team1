package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.book.dto.Cursor;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.entity.QBook;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

  // QueryDSL 쿼리를 실행할 수 있는 객체
  private final JPAQueryFactory queryFactory;
  private final QBook book = QBook.book;

  @Override
  public List<Book> searchBooks(String keyword, String sortBy, Cursor cursor, int limit,
      boolean isAsc) {
    BooleanBuilder condition = new BooleanBuilder();

    // 키워드 검색 -> 제목, 저자 이름, ISBN
    if (StringUtils.hasText(keyword)) {
      condition.andAnyOf(
          book.title.containsIgnoreCase(keyword),
          book.author.containsIgnoreCase(keyword),
          book.isbn.containsIgnoreCase(keyword)
      );
    }

    // 논리 삭제는 제외해야 한다.
    condition.and(book.isDeleted.eq(false));

    // 커서 조건
    if (cursor != null) {
      condition.and(getCursorCondition(sortBy, cursor, isAsc));
    }

    List<OrderSpecifier<?>> orderSpecifiers = buildOrderSpecifiers(sortBy, isAsc);

    return queryFactory
        .selectFrom(book)
        .where(condition)
        .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
        .limit(limit)
        .fetch();
  }

  private BooleanBuilder getCursorCondition(String sortBy, Cursor cursor, boolean isAsc) {
    BooleanBuilder builder = new BooleanBuilder();
    Instant createdAt = cursor.createdAt();

    switch (sortBy) {
      case "title" -> {
        String title = cursor.title();
        if (title != null) {
          builder.and(isAsc
              ? book.title.gt(title).or(book.title.eq(title).and(book.createdAt.gt(createdAt)))
              : book.title.lt(title).or(book.title.eq(title).and(book.createdAt.lt(createdAt)))
          );
        }
      }
      case "rating" -> {
        Double rating = cursor.rating();
        if (rating != null) {
          builder.and(isAsc
              ? book.rating.gt(rating).or(book.rating.eq(rating).and(book.createdAt.gt(createdAt)))
              : book.rating.lt(rating).or(book.rating.eq(rating).and(book.createdAt.lt(createdAt)))
          );
        }
      }
      case "reviewCount" -> {
        Integer reviewCount = cursor.reviewCount();
        if (reviewCount != null) {
          builder.and(isAsc
              ? book.reviewCount.gt(reviewCount).or(book.reviewCount.eq(reviewCount).and(book.createdAt.gt(createdAt)))
              : book.reviewCount.lt(reviewCount).or(book.reviewCount.eq(reviewCount).and(book.createdAt.lt(createdAt)))
          );
        }
      }
      case "publishedDate" -> {
        if (cursor.publishedDate() != null) {
          builder.and(isAsc
              ? book.publishedDate.after(cursor.publishedDate()).or(book.publishedDate.eq(cursor.publishedDate()).and(book.createdAt.gt(createdAt)))
              : book.publishedDate.before(cursor.publishedDate()).or(book.publishedDate.eq(cursor.publishedDate()).and(book.createdAt.lt(createdAt)))
          );
        }
      }
      default -> {
        builder.and(isAsc ? book.createdAt.gt(createdAt) : book.createdAt.lt(createdAt));
      }
    }

    return builder;
  }

  private List<OrderSpecifier<?>> buildOrderSpecifiers(String sortBy, boolean isAsc) {
    List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

    switch (sortBy) {
      case "title" -> orderSpecifiers.add(isAsc ? book.title.asc() : book.title.desc());
      case "publishedDate" -> orderSpecifiers.add(isAsc ? book.publishedDate.asc() : book.publishedDate.desc());
      case "rating" -> orderSpecifiers.add(isAsc ? book.rating.asc() : book.rating.desc());
      case "reviewCount" -> orderSpecifiers.add(isAsc ? book.reviewCount.asc() : book.reviewCount.desc());
      default -> orderSpecifiers.add(isAsc ? book.createdAt.asc() : book.createdAt.desc());
    }

    orderSpecifiers.add(isAsc ? book.createdAt.asc() : book.createdAt.desc());

    return orderSpecifiers;
  }
}
