package com.codeit.duckhu.domain.comments.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class CommentDto {
  UUID id;
  UUID reviewId;
  UUID userId;
  String userNickname;
  String content;
  Instant createdAt;
  Instant updatedAt;

}
