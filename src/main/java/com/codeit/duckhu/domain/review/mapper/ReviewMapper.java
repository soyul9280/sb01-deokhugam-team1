package com.codeit.duckhu.domain.review.mapper;

import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.entity.Review;
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
