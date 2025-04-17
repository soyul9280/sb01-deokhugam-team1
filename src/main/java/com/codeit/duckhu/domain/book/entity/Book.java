package com.codeit.duckhu.domain.book.entity;

import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Book extends BaseUpdatableEntity {

  @Column(nullable = false, length = 50)
  private String title;

  @Column(nullable = false, length = 50)
  private String author;

  @Column(length = 255)
  private String description;

  @Column(nullable = false, length = 50)
  private String publisher;

  @Column(name = "published_date", nullable = false)
  private Instant publishedDate;

  @Column(unique = true, length = 50)
  private String isbn;

  @Column(name = "thumbnail_url", length = 255)
  private String thumbnailUrl;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;
}
