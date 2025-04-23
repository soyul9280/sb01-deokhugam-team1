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
  import java.time.LocalDate;
  import java.util.ArrayList;
  import java.util.Optional;
  import java.util.UUID;
  import org.junit.jupiter.api.Test;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.test.context.ActiveProfiles;
  import org.springframework.test.context.bean.override.mockito.MockitoBean;
/*

  @SpringBootTest
  @ActiveProfiles(profiles = "test")
  class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private ReviewServiceImpl reviewService;

    @MockitoBean
    private CommentMapper commentMapper;


    @Test
    void create(){
      CommentCreateRequest request = new CommentCreateRequest();
      request.setUserId(UUID.randomUUID());
      request.setReviewId(UUID.randomUUID());
      request.setContent("test comment");

      CommentDto dto = new CommentDto();
      dto.setContent("test comment");

      given(userService.findByIdEntityReturn(any(UUID.class))).willReturn(new User(
          "test@mail.com","user","pass",false
      ));
      given(reviewService.findByIdEntityReturn(any(UUID.class))).willReturn(new Review(
          "test",4,5,7,false,null,null
      ));
      given(commentMapper.toDto(any(Comment.class))).willReturn(dto);

      CommentDto commentDto = commentService.create(request);

      assertEquals("test comment", commentDto.getContent());
    }

    @Test
    void update(){
      // given
      UUID commentId = UUID.randomUUID();

      CommentUpdateRequest updateRequest = new CommentUpdateRequest();
      updateRequest.setContent("updated content");

      // 기존 Comment 객체 (DB에서 조회된 상태라 가정)
      Comment existingComment = new Comment();
      existingComment.setContent("old content");

      // 수정 후 DTO (최종 반환 예상값)
      CommentDto updatedDto = new CommentDto();
      updatedDto.setId(commentId);
      updatedDto.setContent("updated content");

      given(commentRepository.findById(any(UUID.class))).willReturn(Optional.of(existingComment));
      given(commentMapper.toDto(existingComment)).willReturn(updatedDto);

      // when
      CommentDto result = commentService.update(commentId, updateRequest);

      // then
      assertEquals("updated content", result.getContent());
      assertEquals(commentId, result.getId());
    }

    @Test
    void delete(){
      UUID commentId = UUID.randomUUID();
      Comment existingComment = new Comment();

      given(commentRepository.findById(any(UUID.class))).willReturn(Optional.of(existingComment));

      commentService.delete(commentId);

      verify(commentRepository).deleteById(any(UUID.class));
    }

    @Test
    void get(){
      UUID commentId = UUID.randomUUID();

      User user = new User("test@mail.com", "user", "pass", false);
      Book book = new Book("title", "author", "desc", "pub", LocalDate.now(), "isbn", "url", false, new ArrayList<>(), new ArrayList<>());
      Review review = new Review("test", 4, 5, 7, user, book);

      Comment comment = new Comment();
      comment.setContent("test comment");

      CommentDto dto = new CommentDto();
      dto.setContent("test comment");


      given(commentRepository.findById(any(UUID.class))).willReturn(Optional.of(comment));
      given(userService.findByIdEntityReturn(any(UUID.class))).willReturn(user);
      given(reviewService.findByIdEntityReturn(any(UUID.class))).willReturn(review);
      given(commentMapper.toDto(comment)).willReturn(dto);

      CommentDto result = commentService.get(commentId);

      assertEquals("test comment", result.getContent());
      assertNotNull(result);
    }


  }*/