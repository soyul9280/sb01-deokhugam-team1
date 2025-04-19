package com.codeit.duckhu.comments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.duckhu.comments.domain.Comment;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.review.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*
@SpringBootTest
public class CommentTest {

  @Test
  void get(){
        User user = new User();
        Comment comment = new Comment(user,new Review(),"content test");

        assertEquals(user, comment.getUser());
  }
}
