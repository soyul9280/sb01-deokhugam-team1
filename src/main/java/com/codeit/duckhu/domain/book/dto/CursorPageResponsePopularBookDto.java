package com.codeit.duckhu.domain.book.dto;

import com.codeit.duckhu.review.dto.ReviewDto;
import java.time.Instant;
import java.util.List;

public record CursorPageResponsePopularBookDto(
    List<ReviewDto> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    int totalElements,
    Boolean hasNext
) {

}
