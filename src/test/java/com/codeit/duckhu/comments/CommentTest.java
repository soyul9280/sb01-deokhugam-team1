package com.codeit.duckhu.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.duckhu.domain.comment.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentTest {

  @Test
  void get(){
        User user = new User();
        Comment comment = Comment.builder()
            .user(user).review(new Review())
            .content("content test").build();

        assertEquals(user, comment.getUser());
  }
}
