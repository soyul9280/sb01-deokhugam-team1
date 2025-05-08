package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.global.type.PeriodType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@StepScope
@Slf4j
public class RankUpdateItemProcessor implements ItemProcessor<PopularReview, PopularReview> {

  @Value("#{jobParameters['period']}")
  private String periodParam;
  
  private int currentRank = 0; // 0으로 초기화하고 첫 process 호출 시 1로 설정
  private boolean isInitialized = false;
  
  public RankUpdateItemProcessor() {
  }
  
  public RankUpdateItemProcessor(String periodParam) {
    this.periodParam = periodParam;
  }

  @Override
  public PopularReview process(PopularReview item) {
    // 첫 process 호출 시 초기화
    if (!isInitialized) {
      currentRank = 1;
      isInitialized = true;
      if (periodParam != null) {
        log.info("인기 리뷰 랭킹 처리 시작: 기간 = {}", periodParam);
      } else {
        log.info("인기 리뷰 랭킹 처리 시작: 기간 미설정");
      }
    }
    
    if (item == null) {
      log.debug("처리할 리뷰가 없습니다.");
      return null;
    }
    
    Double score = item.getScore();
    if (score == null || score <= 0) {
      // ID를 안전하게 추출
      UUID reviewId = item.getReview() != null ? item.getReview().getId() : null;
      
      if (periodParam != null) {
        log.debug("스코어가 0 이하인 인기 리뷰가 랭킹 업데이트에 포함됨: reviewId={}, score={}, 기간={}",
            reviewId, score, periodParam);
      } else {
        log.debug("스코어가 0 이하인 인기 리뷰가 랭킹 업데이트에 포함됨: reviewId={}, score={}",
            reviewId, score);
      }
      return null; // 스코어가 0 이하인 항목은 필터링
    }
    
    item.setRank(currentRank);
    
    // ID를 안전하게 추출
    UUID reviewId = item.getReview() != null ? item.getReview().getId() : null;
    
    if (periodParam != null) {
      log.debug("인기 리뷰 랭킹 설정: rank={}, reviewId={}, score={}, 기간={}", 
          currentRank, reviewId, score, periodParam);
    } else {
      log.debug("인기 리뷰 랭킹 설정: rank={}, reviewId={}, score={}", 
          currentRank, reviewId, score);
    }
    
    currentRank++;
    return item;
  }
}
