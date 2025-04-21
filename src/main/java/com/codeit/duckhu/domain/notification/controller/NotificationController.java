package com.codeit.duckhu.domain.notification.controller;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.dto.NotificationUpdateRequest;
import com.codeit.duckhu.domain.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 특정 알림의 읽음 상태를 업데이트합니다.
     *
     * @param notificationId 읽음 처리할 알림 ID
     * @param userId         요청자 ID (헤더)
     * @param request        읽음 여부 정보
     * @return 업데이트된 알림 정보
     */
    @PatchMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> updateConfirmedStatus(
        @PathVariable UUID notificationId,
        @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
        @RequestBody @Valid NotificationUpdateRequest request
    ) {
        NotificationDto updated = notificationService.updateConfirmedStatus(notificationId, userId,
            request.confirmed());
        return ResponseEntity.ok(updated);
    }

    /**
     * 사용자의 모든 알림을 읽음 상태로 일괄 처리합니다.
     *
     * @param userId 요청자 ID (헤더)
     * @return 204 No Content
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> updateAllConfirmedStatus(
        @RequestHeader("Deokhugam-Request-User-ID") UUID userId
    ) {
        notificationService.updateAllConfirmedStatus(userId);
        return ResponseEntity.noContent().build();
    }
}
