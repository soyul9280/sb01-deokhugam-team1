package com.codeit.duckhu.domain.review.mapper;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

  public ReviewDto toDto(Review review) {
    ReviewDto.ReviewDtoBuilder builder = ReviewDto.builder()
        .id(review.getId())
        .content(review.getContent())
        .rating(review.getRating())
        .likeCount(review.getLikeCount())
        .commentCount(review.getCommentCount())
        .likedByMe(false);  // 현재는 좋아요 기능이 구현되지 않았으므로 기본값 사용
        
    // null 체크를 통한 안전한 접근
    if (review.getUser() != null) {
        builder.userId(review.getUser().getId());
    }
    
    if (review.getBook() != null) {
        builder.bookId(review.getBook().getId());
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
        .build();
  }
}
