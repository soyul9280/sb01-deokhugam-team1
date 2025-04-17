package com.codeit.duckhu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  void get(){
        Comment comment = new Comment();
        User user = new User();
        comment.setUser(user);
        comment.setReview(new Review());
        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        assertEquals(user, comment.getUser());
  }
}
