package com.codeit.duckhu.domain.notification.controller;

import com.codeit.duckhu.domain.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.dto.NotificationUpdateRequest;
import com.codeit.duckhu.domain.notification.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public CursorPageResponseNotificationDto getNotifications(
        @RequestParam UUID userId,
        @RequestParam(defaultValue = "DESC") String direction,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cursor,
        @RequestParam(defaultValue = "20") int limit) {

        log.info("API 호출: GET /api/notifications, userId={}, direction={}, cursor={}, limit={}",
            userId, direction, cursor, limit);

        CursorPageResponseNotificationDto dto = notificationService
            .getNotifications(userId, direction, cursor, limit);

        log.info("API 응답 준비 완료: userId={}, returnedCount={}, hasNext={}",
            userId, dto.content().size(), dto.hasNext());

        return dto;
    }

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
        @RequestBody @Valid NotificationUpdateRequest request) {
        NotificationDto updated =
            notificationService.updateConfirmedStatus(notificationId, userId, request.confirmed());
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
        @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
        notificationService.updateAllConfirmedStatus(userId);
        return ResponseEntity.noContent().build();
    }
}
