package com.codeit.duckhu.domain.review.entity;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends BaseUpdatableEntity {

  @Column(name = "content", nullable = false)
  @NotBlank
  private String content;

  @Column(name = "rating", nullable = false)
  @Min(1)
  @Max(5)
  @NotNull
  private int rating;

  @Column(name = "like_count", nullable = false)
  @Builder.Default
  private int likeCount = 0;

  @Column(name = "comment_count", nullable = false)
  @Builder.Default
  private int commentCount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn
  private Book book;

  public void updateContent(String content) {
    this.content = content;
  }
  public void updateRating(int rating) {
    this.rating = rating;
  }
}
