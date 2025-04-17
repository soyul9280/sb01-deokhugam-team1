package com.codeit.duckhu.comments;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommentServiceTest {

  private CommentService commentService;

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
    commentService.create(request);

    CommentUpdateRequest updateRequest = new CommentUpdateRequest();
    updateRequest.setContent("update test comment");

    CommentDto commentDto = commentService.update(updateRequest);

    assertEquals(commentDto.getContent,"update test comment");
  }

  @Test
  void delete(){
    CommentCreateRequest request = new CommentCreateRequest();
    request.setContent("test comment");
    CommentDto commentDto = commentService.create(request);

    commentService.delete(commentDto.getId());

    Comment deletedComment = commentRepository.findById(commentDto.getId()).orElseThrow();

    assertTrue(deletedComment.isDeleted(), "댓글은 논리적으로 삭제되어야 한다");
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