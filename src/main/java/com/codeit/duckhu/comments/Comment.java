package com.codeit.duckhu.comments;


import com.codeit.duckhu.global.entity.BaseEntity;
import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment extends BaseUpdatableEntity {

  @OneToMany
  User user;

  @OneToMany
  Review review;

  @Column(name = "content")
  String content;
}
