package com.codeit.duckhu.domain.comments.service;


import com.codeit.duckhu.domain.comments.exception.NoCommentException;
import com.codeit.duckhu.domain.comments.domain.Comment;
import com.codeit.duckhu.domain.comments.dto.CommentDto;
import com.codeit.duckhu.domain.comments.repository.CommentRepository;
import com.codeit.duckhu.domain.comments.dto.request.CommentCreateRequest;
import com.codeit.duckhu.domain.comments.dto.request.CommentUpdateRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository repository;
  private final CommentMapper commentMapper;

   public CommentDto get(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException("해당하는 댓글이 없습니다.");
     }

     Comment comment = repository.findById(id).get();

     return commentMapper.toDto(comment);
   }

   //TODO : User & Review service 이용하여 객체 불러오기 필요
   public CommentDto create(CommentCreateRequest request){
     Comment comment = new Comment();

     repository.save(comment);

     return commentMapper.toDto(comment);
   }

   public void delete(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException("해당하는 댓글이 없습니다.");
     }

     repository.deleteById(id);
   }

   public CommentDto update(UUID id, CommentUpdateRequest request){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException("해당하는 댓글이 없습니다.");
     }

     Comment comment = repository.findById(id).get();

     comment.setContent(request.getContent());
     repository.save(comment);

     return  commentMapper.toDto(comment);
   }
}
