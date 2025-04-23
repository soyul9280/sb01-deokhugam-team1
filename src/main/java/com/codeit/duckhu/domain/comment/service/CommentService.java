package com.codeit.duckhu.domain.comment.service;


import com.codeit.duckhu.domain.comment.exception.NoAuthorityException;
import com.codeit.duckhu.domain.comment.exception.NoCommentException;
import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.dto.CommentDto;
import com.codeit.duckhu.domain.comment.repository.CommentRepository;
import com.codeit.duckhu.domain.comment.dto.request.CommentCreateRequest;
import com.codeit.duckhu.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
import com.codeit.duckhu.domain.user.service.UserServiceImpl;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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


   public CommentDto create(CommentCreateRequest request){
     Comment comment = Comment.builder()
         .user(userService.findByIdEntityReturn(request.getUserId()))
         .review(reviewService.findByIdEntityReturn(request.getReviewId()))
         .content(request.getContent())
         .build();

     repository.save(comment);

     return commentMapper.toDto(comment);
   }

   public void delete(UUID id, UUID userId){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

     if(comment.getUser().getId().equals(userId)) {
       repository.deleteById(id);
     }
     else{
       throw new NoAuthorityException(ErrorCode.NO_AUTHORITY_USER);
     }
   }

   public void deleteSoft(UUID id, UUID userId){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

     if(comment.getUser().getId().equals(userId)){
       comment.markAsDeleted(true);
     }
     else{
       throw new NoAuthorityException(ErrorCode.NO_AUTHORITY_USER);
     }
   }

   public CommentDto update(UUID id, CommentUpdateRequest request, UUID userId){
     Comment comment = repository.findById(id)
         .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

     if(comment.getUser().getId().equals(userId)){
       comment.setContent(request.getContent());
     }
     else{
       throw new NoAuthorityException(ErrorCode.NO_AUTHORITY_USER);
     }

     return  commentMapper.toDto(comment);
   }
}
