package com.codeit.duckhu.domain.book.dto;

import com.codeit.duckhu.domain.review.dto.ReviewDto;
import java.time.Instant;
import java.util.List;

public record CursorPageResponsePopularBookDto(
    List<ReviewDto> content,
    String nextCursor,
    Instant nextAfter,
    Integer size,
    Integer totalElements,
    Boolean hasNext
) {

}
