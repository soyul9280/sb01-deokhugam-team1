package com.codeit.duckhu.domain.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageResponseReviewDto {

  private List<ReviewDto> reviews;
  private String nextCursor;
  private LocalDateTime nextAfter;
  private int size;
  private Long totalElements;
  private boolean hasNext;
}
