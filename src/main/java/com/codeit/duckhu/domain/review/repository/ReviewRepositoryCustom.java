package com.codeit.duckhu.domain.review.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReviewRepositoryCustom {
  Map<UUID, Integer> countByBookIds(List<UUID> bookIds);

  Map<UUID, Double> averageRatingByBookIds(List<UUID> bookIds);

}
