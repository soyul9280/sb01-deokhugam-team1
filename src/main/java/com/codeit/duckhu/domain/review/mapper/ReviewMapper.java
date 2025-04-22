package com.codeit.duckhu.domain.review.mapper;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {

  @Mapping(target = "userId", source = "review.user.id")
  @Mapping(target = "userNickname", source = "review.user.nickname")
  @Mapping(target = "bookId", source = "review.book.id")
  @Mapping(target = "bookTitle", source = "review.book.title")
  @Mapping(target = "likedByMe", constant = "false")
  ReviewDto toDto(Review review);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", source = "user")
  @Mapping(target = "book", source = "book")
  @Mapping(target = "content", source = "request.content")
  @Mapping(target = "rating", source = "request.rating")
  @Mapping(target = "likeCount", constant = "0")
  @Mapping(target = "commentCount", constant = "0")
  @Mapping(target = "isDeleted", constant = "false")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Review toEntity(ReviewCreateRequest request, User user, Book book);
}
