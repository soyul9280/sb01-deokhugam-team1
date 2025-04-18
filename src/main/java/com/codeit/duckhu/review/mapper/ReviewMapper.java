package com.codeit.duckhu.review.mapper;

import com.codeit.duckhu.review.dto.ReviewDto;
import com.codeit.duckhu.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

  public ReviewDto toDto(Review review) {
    return ReviewDto.builder()
        .content(review.getContent())
        .rating(review.getRating())
        .build();
  }
}
