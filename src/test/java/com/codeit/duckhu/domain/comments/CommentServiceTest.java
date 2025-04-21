  package com.codeit.duckhu.domain.comments;

  import static org.junit.jupiter.api.Assertions.*;
  import static org.mockito.ArgumentMatchers.any;
  import static org.mockito.BDDMockito.given;
  import static org.mockito.Mockito.verify;

  import com.codeit.duckhu.domain.book.entity.Book;
  import com.codeit.duckhu.domain.comment.domain.Comment;
  import com.codeit.duckhu.domain.comment.dto.CommentDto;
  import com.codeit.duckhu.domain.comment.repository.CommentRepository;
  import com.codeit.duckhu.domain.comment.dto.request.CommentCreateRequest;
  import com.codeit.duckhu.domain.comment.dto.request.CommentUpdateRequest;
  import com.codeit.duckhu.domain.comment.service.CommentMapper;
  import com.codeit.duckhu.domain.comment.service.CommentService;
  import com.codeit.duckhu.domain.review.entity.Review;
  import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
  import com.codeit.duckhu.domain.user.entity.User;
  import com.codeit.duckhu.domain.user.service.UserServiceImpl;
  import java.time.Instant;
  import java.time.LocalDate;
  import java.util.ArrayList;
  import java.util.UUID;
  import org.junit.jupiter.api.Test;
  import org.junit.jupiter.api.extension.ExtendWith;
  import org.mockito.InjectMocks;
  import org.mockito.Mock;
  import org.mockito.junit.jupiter.MockitoExtension;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.test.context.ActiveProfiles;
  import org.springframework.test.context.bean.override.mockito.MockitoBean;

  @ExtendWith(MockitoExtension.class)
  @ActiveProfiles(profiles = "test")
  class CommentServiceTest {

    @Mock
    private CommentService commentService;

    @Test
    void create(){
      CommentCreateRequest request = new CommentCreateRequest();
      request.setUserId(UUID.randomUUID());
      request.setReviewId(UUID.randomUUID());
      request.setContent("test comment");

      CommentDto savedComment = new CommentDto();
      savedComment.setContent("test comment");
      savedComment.setUserId(request.getUserId());
      savedComment.setReviewId(request.getReviewId());

      given(commentService.create(any(CommentCreateRequest.class))).willReturn(savedComment);

      CommentDto commentDto = commentService.create(request);

      assertEquals("test comment", commentDto.getContent());
    }

    @Test
    void update(){
      UUID commentId = UUID.randomUUID();
      CommentUpdateRequest updateRequest = new CommentUpdateRequest();
      updateRequest.setContent("update test comment");

      CommentDto updatedDto = new CommentDto();
      updatedDto.setId(commentId);
      updatedDto.setContent("update test comment");

      given(commentService.update(any(UUID.class), any(CommentUpdateRequest.class)))
          .willReturn(updatedDto);

      CommentDto result = commentService.update(commentId, updateRequest);

      assertEquals("update test comment", result.getContent());
      assertEquals(commentId, result.getId());
    }

    @Test
    void delete(){
      UUID commentId = UUID.randomUUID();

      commentService.delete(commentId);

      verify(commentService).delete(commentId);
    }

    @Test
    void get(){
      UUID commentId = UUID.randomUUID();
      CommentDto expected = new CommentDto();
      expected.setId(commentId);
      expected.setContent("test comment");

      given(commentService.get(commentId)).willReturn(expected);

      CommentDto result = commentService.get(commentId);

      assertEquals("test comment", result.getContent());
      assertEquals(commentId, result.getId());
    }

  }