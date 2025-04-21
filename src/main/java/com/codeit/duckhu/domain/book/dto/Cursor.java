package com.codeit.duckhu.domain.book.dto;

import java.time.Instant;
import java.time.LocalDate;

public record Cursor(
    String title,
    Double rating,
    Integer reviewCount,
    LocalDate publishedDate,
    Instant createdAt
) {

}
