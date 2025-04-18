package com.codeit.duckhu.domain.book.dto;

import com.codeit.duckhu.domain.review.dto.ReviewDto;
import java.time.Instant;
import java.util.List;

public record CursorPageResponseBookDto(
    List<ReviewDto> content,
    String nextCursor,
    Instant nextAfter,
    int size,
    int totalElements,
    Boolean hasNext
) {

}
