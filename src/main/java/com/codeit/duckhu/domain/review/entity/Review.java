package com.codeit.duckhu.domain.review.entity;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


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
  
  @ElementCollection
  @CollectionTable(name = "review_likes", joinColumns = @JoinColumn(name = "review_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"review_id", "user_id"}))
  @Column(name = "user_id")
  @Builder.Default
  private Set<UUID> likedUserIds = new HashSet<>();

  public void updateContent(String content) {
    this.content = content;
  }
  public void updateRating(int rating) {
    this.rating = rating;
  }
  
  public void increaseLikeCount(UUID userId) {
    if (!this.likedUserIds.contains(userId)) {
      this.likedUserIds.add(userId);
      this.likeCount++;
    }
  }

  public void decreaseLikeCount(UUID userId) {
    if (this.likedUserIds.contains(userId)) {
      this.likedUserIds.remove(userId);
      if (this.likeCount > 0) {
        this.likeCount--;
      }
    }
  }
  
  public boolean liked(UUID userId) {
    return this.likedUserIds.contains(userId);
  }
  
  public boolean toggleLike(UUID userId) {
    if (liked(userId)) {
      decreaseLikeCount(userId);
      return false;
    } else {
      increaseLikeCount(userId);
      return true;
    }
  }
}
