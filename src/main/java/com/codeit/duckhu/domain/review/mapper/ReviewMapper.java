package com.codeit.duckhu.domain.review.mapper;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

  public ReviewDto toDto(Review review) {
    ReviewDto.ReviewDtoBuilder builder =
        ReviewDto.builder()
            .id(review.getId())
            .content(review.getContent())
            .rating(review.getRating())
            .likeCount(review.getLikeCount())
            .commentCount(review.getCommentCount())
            .likedByMe(false); // 현재는 좋아요 기능이 구현되지 않았으므로 기본값 사용

    // null 체크를 통한 안전한 접근
    if (review.getUser() != null) {
      builder.userId(review.getUser().getId()).userNickname(review.getUser().getNickname());
    }

    if (review.getBook() != null) {
      builder
          .bookId(review.getBook().getId())
          .bookTitle(review.getBook().getTitle())
          .bookThumbnailUrl(review.getBook().getThumbnailUrl());
    }

    // 날짜 처리
    if (review.getCreatedAt() != null) {
      builder.createdAt(mapInstantToLocalDateTime(review.getCreatedAt()));
    }

    if (review.getUpdatedAt() != null) {
      builder.updatedAt(mapInstantToLocalDateTime(review.getUpdatedAt()));
    }

    return builder.build();
  }

  public Review toEntity(ReviewCreateRequest request, User user, Book book) {
    return Review.builder()
        .user(user)
        .book(book)
        .content(request.getContent())
        .rating(request.getRating())
        .likeCount(0)
        .commentCount(0)
        .isDeleted(false)
        .build();
  }

  private LocalDateTime mapInstantToLocalDateTime(Instant instant) {
    return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}
