package com.codeit.duckhu.domain.comment.service;


import com.codeit.duckhu.domain.comment.exception.NoCommentException;
import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.dto.CommentDto;
import com.codeit.duckhu.domain.comment.repository.CommentRepository;
import com.codeit.duckhu.domain.comment.dto.request.CommentCreateRequest;
import com.codeit.duckhu.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.duckhu.domain.user.service.UserServiceImpl;
import com.codeit.duckhu.review.service.impl.ReviewServiceImpl;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
  private final CommentRepository repository;
  private final CommentMapper commentMapper;

  private final UserServiceImpl userService;
  private final ReviewServiceImpl reviewService;

   public CommentDto get(UUID id){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));


     return commentMapper.toDto(comment);
   }

   public List<CommentDto> getList(UUID reviewId,String direction,
       UUID cursorId, Instant createdAt, int limit){
     Slice<Comment> list = repository.searchAll(reviewId,direction,createdAt,cursorId,limit);

     return list.getContent().stream()
         .map(commentMapper::toDto).toList();
   }

   //TODO : User & Review service 이용하여 객체 불러오기 필요 : 현재 메서드 미구현 상태
   public CommentDto create(CommentCreateRequest request){
     Comment comment = Comment.builder()
         .user()
         .review()
         .content(request.getContent())
         .build();

     repository.save(comment);

     return commentMapper.toDto(comment);
   }

   public void delete(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException(ErrorCode.NOT_FOUND_COMMENT);
     }

     repository.deleteById(id);
   }

   public void deleteSoft(UUID id){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

     comment.markAsDeleted(true);
   }

   public CommentDto update(UUID id, CommentUpdateRequest request){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

     comment.setContent(request.getContent());

     return  commentMapper.toDto(comment);
   }
}
