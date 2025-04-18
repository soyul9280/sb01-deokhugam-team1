package com.codeit.duckhu.domain.comment.dto.request;

import java.util.UUID;
import lombok.Data;

@Data
public class CommentCreateRequest {
  UUID userId;
  UUID reviewId;
  String content;
}
