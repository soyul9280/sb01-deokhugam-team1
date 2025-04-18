package com.codeit.duckhu.domain.comments;

import static org.junit.jupiter.api.Assertions.*;

import com.codeit.duckhu.domain.comments.domain.Comment;
import com.codeit.duckhu.domain.comments.dto.CommentDto;
import com.codeit.duckhu.domain.comments.repository.CommentRepository;
import com.codeit.duckhu.domain.comments.dto.request.CommentCreateRequest;
import com.codeit.duckhu.domain.comments.dto.request.CommentUpdateRequest;
import com.codeit.duckhu.domain.comments.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentServiceTest {

  @Autowired
  private CommentService commentService;

  @Autowired
  private CommentRepository commentRepository;

  @Test
  void create(){
    CommentCreateRequest request = new CommentCreateRequest();
    request.setContent("test comment");

    CommentDto commentDto = commentService.create(request);

    assertEquals(commentDto.getContent(), "test comment");
  }

  @Test
  void update(){
    CommentCreateRequest request = new CommentCreateRequest();
    request.setContent("test comment");
    CommentDto commentDto1 = commentService.create(request);

    CommentUpdateRequest updateRequest = new CommentUpdateRequest();
    updateRequest.setContent("update test comment");

    CommentDto commentDto = commentService.update(commentDto1.getId(),updateRequest);

    assertEquals("update test comment", commentDto.getContent());
  }

  @Test
  void delete(){
    CommentCreateRequest request = new CommentCreateRequest();
    request.setContent("test comment");
    CommentDto commentDto = commentService.create(request);

    commentService.delete(commentDto.getId());

    Comment deletedComment = commentRepository.findById(commentDto.getId()).orElseThrow();

    assertTrue(deletedComment.getIsDeleted(), "댓글은 논리적으로 삭제되어야 한다");
  }

  @Test
  void get(){
    CommentCreateRequest request = new CommentCreateRequest();
    request.setContent("test comment");
    CommentDto commentDto = commentService.create(request);

    commentService.delete(commentDto.getId());

    CommentDto commentDto1 = commentService.get(commentDto.getId());

    assertEquals(commentDto.getContent(), commentDto1.getContent());
  }

}