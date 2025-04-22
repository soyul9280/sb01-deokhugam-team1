package com.codeit.duckhu.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.review.entity.Review;

import org.junit.jupiter.api.Test;

/*
public class CommentTest {

  @Test
  void createEntity(){
    User user = User.builder()
        .email("test@email.com")
        .nickname("user1")
        .password("password")
        .isDeleted(false)
        .build();

    Review review = Review.builder()
        .content("new review")
        .rating(3)
        .likeCount(3)
        .commentCount(5)
        .user(user)
        .build();

    Comment comment = Comment.builder()
        .user(user)
        .review(new Review("new review",3,3,5,user,null))
        .content("content test").build();

    assertEquals(user, comment.getUser());
  }
}*/
