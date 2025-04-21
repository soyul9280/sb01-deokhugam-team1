package com.codeit.duckhu.domain.comment.service;


import com.codeit.duckhu.domain.comment.exception.NoCommentException;
import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.dto.CommentDto;
import com.codeit.duckhu.domain.comment.repository.CommentRepository;
import com.codeit.duckhu.domain.comment.dto.request.CommentCreateRequest;
import com.codeit.duckhu.domain.comment.dto.request.CommentUpdateRequest;
import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
import com.codeit.duckhu.domain.user.service.UserServiceImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository repository;
  private final CommentMapper commentMapper;

  private final UserServiceImpl userService;
  private final ReviewServiceImpl reviewService;

   public CommentDto get(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException(ErrorCode.NOT_FOUND_COMMENT);
     }

     Comment comment = repository.findById(id).get();

     return commentMapper.toDto(comment);
   }

//   public Slice<CommentDto> getList(){
//
//   }
//
//   //TODO : User & Review service 이용하여 객체 불러오기 필요 : 현재 메서드 미구현 상태
//   public CommentDto create(CommentCreateRequest request){
//     Comment comment = Comment.builder()
//         .user()
//         .review()
//         .content(request.getContent())
//         .build();
//
//     repository.save(comment);
//
//     return commentMapper.toDto(comment);
//   }

   public void delete(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException(ErrorCode.NOT_FOUND_COMMENT);
     }

     repository.deleteById(id);
   }

   public void deleteSoft(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException(ErrorCode.NOT_FOUND_COMMENT);
     }

     Comment comment = repository.findById(id).get();
     comment.markAsDeleted(true);

     repository.save(comment);
   }

   public CommentDto update(UUID id, CommentUpdateRequest request){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException(ErrorCode.NOT_FOUND_COMMENT);
     }

     Comment comment = repository.findById(id).get();

     comment.setContent(request.getContent());
     repository.save(comment);

     return  commentMapper.toDto(comment);
   }
}
