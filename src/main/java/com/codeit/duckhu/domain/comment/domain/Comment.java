package com.codeit.duckhu.domain.comment.domain;

import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment extends BaseUpdatableEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @Builder
  public Comment(User user, Review review, String content, Boolean isDeleted) {
    this.user = user;
    this.review = review;
    this.content = content;
    this.isDeleted = (isDeleted != null) ? isDeleted : false;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void markAsDeleted(Boolean deleted) {
    isDeleted = deleted;
  }
}
