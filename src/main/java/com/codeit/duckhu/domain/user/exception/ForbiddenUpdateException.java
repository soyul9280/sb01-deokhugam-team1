package com.codeit.duckhu.domain.user.exception;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class ForbiddenUpdateException extends UserException{
    public ForbiddenUpdateException(UUID loginId,UUID targetId) {
        super(Instant.now(),UserErrorCode.UNAUTHORIZED_UPDATE,
                Map.of("targetId",targetId.toString(),"loginId",loginId.toString()));

}}

