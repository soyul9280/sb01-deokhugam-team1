package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.domain.review.entity.PopularReview;
import com.codeit.duckhu.global.exception.DomainException;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.codeit.duckhu.global.type.PeriodType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;

@StepScope
@RequiredArgsConstructor
@Slf4j
public class RankUpdateItemReader extends JpaPagingItemReader<PopularReview> {

  @Value("#{jobParameters['period']}")
  private String periodParam;

  private final EntityManagerFactory entityManagerFactory;

  @PostConstruct
  public void init() {
    setEntityManagerFactory(entityManagerFactory);
    setQueryString("SELECT p FROM PopularReview p WHERE p.period = :period AND p.score > 0 ORDER BY p.score DESC, p.id ASC");
    setPageSize(100);
    try {
      setParameterValues(Map.of("period", PeriodType.valueOf(periodParam)));
    } catch (IllegalArgumentException e) {
      throw new DomainException(ErrorCode.BATCH_PARAMETER_ERROR);
    }
  }

  public void setPeriodParam(String periodParam) {
    this.periodParam = periodParam;
  }
}
