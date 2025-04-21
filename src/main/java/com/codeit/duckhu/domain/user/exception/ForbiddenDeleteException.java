package com.codeit.duckhu.domain.user.exception;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ForbiddenDeleteException extends UserException{
    public ForbiddenDeleteException(UUID loginId, UUID targetId) {
        super(Instant.now(),UserErrorCode.UNAUTHORIZED_DELETE,
                Map.of("targetId",targetId.toString(),"loginId",loginId.toString()));

    }}

