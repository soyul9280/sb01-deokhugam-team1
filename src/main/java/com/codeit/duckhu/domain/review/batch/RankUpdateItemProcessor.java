package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import org.springframework.batch.item.ItemProcessor;

public class RankUpdateItemProcessor implements ItemProcessor<PopularReview, PopularReview> {

  private int currentRank = 1;

  @Override
  public PopularReview process(PopularReview item) {
    item.setRank(currentRank++);
    return item;
  }
}
