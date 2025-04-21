<<<<<<<< HEAD:src/main/java/com/codeit/duckhu/domain/comment/service/CommentMapper.java
<<<<<<<< HEAD:src/main/java/com/codeit/duckhu/domain/comments/service/CommentMapper.java
========
>>>>>>>> b1019e7 (fix : Review 엔티티 likedByMe 삭제):src/main/java/com/codeit/duckhu/domain/comments/service/CommentMapper.java
package com.codeit.duckhu.domain.comments.service;

import com.codeit.duckhu.domain.comments.domain.Comment;
import com.codeit.duckhu.domain.comments.dto.CommentDto;
<<<<<<<< HEAD:src/main/java/com/codeit/duckhu/domain/comment/service/CommentMapper.java
========
package com.codeit.duckhu.domain.comment.service;

import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.dto.CommentDto;
>>>>>>>> 131530772d8d3af4cc636abca04259c6c4f8bd51:src/main/java/com/codeit/duckhu/domain/comment/service/CommentMapper.java
========
>>>>>>>> b1019e7 (fix : Review 엔티티 likedByMe 삭제):src/main/java/com/codeit/duckhu/domain/comments/service/CommentMapper.java
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

  @Mapping(target = "userNickname" , expression = "java(userName(comment))")
  @Mapping(target = "userId", expression = "java(userId(comment))")
  @Mapping(target = "reviewId", expression = "java(reviewId(comment))")
  abstract CommentDto toDto(Comment comment);

  UUID reviewId(Comment comment){
    return comment.getReview().getId();
  }

  UUID userId(Comment comment){
    return comment.getUser().getId();
  }

  String userName(Comment comment){
    return comment.getUser().getNickname();
  }

}