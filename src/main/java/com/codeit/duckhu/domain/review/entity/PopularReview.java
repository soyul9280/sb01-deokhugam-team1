package com.codeit.duckhu.domain.review.entity;

import com.codeit.duckhu.global.entity.BaseEntity;
import com.codeit.duckhu.global.type.PeriodType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity @Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "popular_reviews")
public class PopularReview extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "review_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_popularreview_review"))
  private Review review;

  @Column(name = "review_rating", nullable = false)
  private Double reviewRating;

  @Enumerated(EnumType.STRING)
  @Column(name = "period", nullable = false)
  private PeriodType period;

  @Column(name = "like_count")
  private Integer likeCount;

  @Column(name = "comment_count")
  private Integer commentCount;

  @Column(name = "score", nullable = false)
  private Double score;

  @Column(name = "rank")
  private Integer rank;
}
