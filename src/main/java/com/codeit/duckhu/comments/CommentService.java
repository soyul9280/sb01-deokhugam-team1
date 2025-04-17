package com.codeit.duckhu.comments;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final CommentRepository repository;

   public CommentDto get(UUID id){
     if(repository.findById(id).isEmpty()){
       throw new NoCommentException("해당하는 댓글이 없습니다.");
     }

     Comment comment = repository.findById(id).get();

     return null;
   }
}
