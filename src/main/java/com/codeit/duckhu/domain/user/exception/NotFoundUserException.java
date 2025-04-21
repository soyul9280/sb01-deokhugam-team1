package com.codeit.duckhu.domain.user.exception;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class NotFoundUserException extends UserException {
    public NotFoundUserException(UUID id) {
        super(Instant.now(),UserErrorCode.NOT_FOUND_USER, Map.of("id", id));
    }
}
