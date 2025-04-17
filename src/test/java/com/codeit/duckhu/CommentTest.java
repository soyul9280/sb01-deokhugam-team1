package com.codeit.duckhu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.duckhu.comments.Comment;
import java.time.Instant;
import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  void get(){
        User user = new User();
        Comment comment = new Comment(user,new Review(),"content test");

        assertEquals(user, comment.getUser());
  }
}
