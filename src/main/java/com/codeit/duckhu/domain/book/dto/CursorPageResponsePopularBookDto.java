package com.codeit.duckhu.domain.book.dto;

import java.time.Instant;

public record CursorPageResponsePopularBookDto(
//    List<ReviewDto> content, -> TODO
    String nextCursor,
    Instant nextAfter,
    Integer size,
    Integer totalElements,
    Boolean hasNext
) {

}
