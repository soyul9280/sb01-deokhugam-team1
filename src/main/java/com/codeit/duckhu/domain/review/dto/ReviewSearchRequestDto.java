package com.codeit.duckhu.domain.review.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSearchRequestDto {
  private String keyword;

  @Builder.Default private String orderBy = "createdAt";

  @Builder.Default private String direction = "DESC";

  private UUID userId;
  private UUID bookId;
  private String cursor;
  private Instant after;

  @Builder.Default private int limit = 50;
}
