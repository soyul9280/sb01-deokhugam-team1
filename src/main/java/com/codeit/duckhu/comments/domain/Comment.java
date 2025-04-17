package com.codeit.duckhu.comments.domain;


import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
public class Comment extends BaseUpdatableEntity {

  @ManyToOne
  User user;

  @ManyToOne
  Review review;

  @Column(name = "content")
  String content;

  @Column(name = "is_deleted")
  Boolean isDeleted;

  public Comment(User user, Review review, String content) {
    this.user = user;
    this.review = review;
    this.content = content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
