package com.codeit.duckhu.domain.notification.exception;

import com.codeit.duckhu.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode{

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림을 찾을 수 없습니다.", "존재하지 않는 알림입니다."),
    NOTIFICATION_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이미 확인된 알림입니다.", "해당 알림은 이미 확인되었습니다."),
    FAILED_TO_CREATE_NOTIFICATION(HttpStatus.INTERNAL_SERVER_ERROR, "알림 생성에 실패했습니다.", "알림 저장 중 오류가 발생했습니다."),
    INVALID_NOTIFICATION_RECEIVER(HttpStatus.BAD_REQUEST, "수신자 정보가 잘못되었습니다.", "알림 수신자 정보가 유효하지 않습니다.");

    private final HttpStatus status;
    private final String message;
    private final String detail;
}
