package com.codeit.duckhu.domain.user.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PowerUserStatsDto(
        UUID userId,
        Double reviewScoreSum,
        Long likedCount,
        Long commentCount
) {
}
