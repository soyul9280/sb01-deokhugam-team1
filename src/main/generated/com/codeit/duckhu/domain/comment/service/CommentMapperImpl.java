package com.codeit.duckhu.domain.comment.service;

import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.dto.CommentDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-22T13:14:20+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class CommentMapperImpl extends CommentMapper {

    @Override
    public CommentDto toDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDto commentDto = new CommentDto();

        commentDto.setId( comment.getId() );
        commentDto.setContent( comment.getContent() );
        commentDto.setCreatedAt( comment.getCreatedAt() );
        commentDto.setUpdatedAt( comment.getUpdatedAt() );

        commentDto.setUserNickname( userName(comment) );
        commentDto.setUserId( userId(comment) );
        commentDto.setReviewId( reviewId(comment) );

        return commentDto;
    }
}
