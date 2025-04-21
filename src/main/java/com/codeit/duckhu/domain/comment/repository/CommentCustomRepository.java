package com.codeit.duckhu.domain.comment.repository;

import com.codeit.duckhu.domain.comment.domain.Comment;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentCustomRepository {
  public Slice<Comment> searchAll(UUID reviewId, String direction, Instant after, UUID cursorId, int limit);
}
