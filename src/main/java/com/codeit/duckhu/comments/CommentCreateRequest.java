package com.codeit.duckhu.comments;

import java.util.UUID;
import lombok.Data;

@Data
public class CommentCreateRequest {
  UUID userId;
  UUID reviewId;
  String content;
}
