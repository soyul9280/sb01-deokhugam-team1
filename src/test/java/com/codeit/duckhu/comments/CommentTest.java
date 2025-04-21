package com.codeit.duckhu.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.user.entity.User;
import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  void createEntity(){
    User user = new User("test@email.com","user1","password",false);
    Comment comment = Comment.builder()
        .user(user).review(new Review("new review",3,3,5,false))
        .content("content test").build();

    assertEquals(user, comment.getUser());
  }
}