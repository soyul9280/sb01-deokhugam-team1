package com.codeit.duckhu.domain.notification.mapper;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    //reviewTitle은 현재 엔티티에 담지 않아 따로 mapping이 필요하여 추가
    @Mapping(target = "reviewTitle", expression = "java(getReviewTitle(notification))")
    NotificationDto toDto(Notification notification);

    //default 메서드로 리뷰 제목 매핑 로직
    default String getReviewTitle(Notification notification) {
        // Todo: 리뷰 ID 기반으로 실제 리뷰 제목 조회하도록 리팩토링
        return null;
    }
}
