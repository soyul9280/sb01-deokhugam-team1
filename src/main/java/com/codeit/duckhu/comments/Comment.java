//package com.codeit.duckhu.comments;
//
//
//import com.codeit.duckhu.global.entity.BaseEntity;
//import com.codeit.duckhu.global.entity.BaseUpdatableEntity;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.Table;
//import java.util.UUID;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "comments")
//@Getter
//@NoArgsConstructor
//public class Comment extends BaseUpdatableEntity {
//
//  @ManyToOne
//  User user;
//
//  @ManyToOne
//  Review review;
//
//  @Column(name = "content")
//  String content;
//
//  public Comment(User user, Review review, String content) {
//    this.user = user;
//    this.review = review;
//    this.content = content;
//  }
//}
