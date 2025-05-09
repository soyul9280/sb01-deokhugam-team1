package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.domain.review.repository.PopularReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@StepScope
@Component
@Slf4j
public class RankUpdateItemWriter implements ItemWriter<PopularReview> {

  private final PopularReviewRepository popularReviewRepository;

  @Override
  public void write(Chunk<? extends PopularReview> chunk) {
    log.info("인기 리뷰 랭킹 업데이트: {} 건의 리뷰 저장", chunk.size());
    popularReviewRepository.saveAll(chunk.getItems());
  }
}
