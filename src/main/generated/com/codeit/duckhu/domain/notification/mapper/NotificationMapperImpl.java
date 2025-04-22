package com.codeit.duckhu.domain.notification.mapper;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-22T13:14:20+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        UUID id = null;
        UUID reviewId = null;
        String content = null;
        boolean confirmed = false;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = notification.getId();
        reviewId = notification.getReviewId();
        content = notification.getContent();
        confirmed = notification.isConfirmed();
        createdAt = notification.getCreatedAt();
        updatedAt = notification.getUpdatedAt();

        String reviewTitle = getReviewTitle(notification);
        UUID userId = null;

        NotificationDto notificationDto = new NotificationDto( id, userId, reviewId, reviewTitle, content, confirmed, createdAt, updatedAt );

        return notificationDto;
    }
}
