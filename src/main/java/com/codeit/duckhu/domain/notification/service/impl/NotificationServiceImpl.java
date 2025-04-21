package com.codeit.duckhu.domain.notification.service.impl;

import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.exception.NotificationAccessDeniedException;
import com.codeit.duckhu.domain.notification.exception.NotificationNotFoundException;
import com.codeit.duckhu.domain.notification.mapper.NotificationMapper;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.repository.NotificationRepsitory;
import com.codeit.duckhu.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepsitory notificationRepsitory;
    private final NotificationMapper notificationMapper;

    /**
     * 내가 작성한 리뷰에 다른 사용자가 좋아요를 누르면 알림을 생성한다.
     *
     * @param reviewId      알림 대상 리뷰 ID
     * @param triggerUserId 좋아요를 누른 사용자 ID
     * @param receiverId    알림을 받을 사용자 ID (리뷰 작성자)
     * @return 생성된 알림 객체
     */
    @Override
    @Transactional
    public NotificationDto createNotifyByLike(UUID reviewId, UUID triggerUserId, UUID receiverId) {
        // Todo 실제 triggerNickname,receiverid는 각각 review, User에서 조회할수 있어야 된다(리팩토링)
        String triggerNickname = "buzz"; // 임시

        // Todo
        // 현재는 자기 자신에게 좋아요/댓글을 남겨도 알림이 생성되도록 설계되어 있음.
        // 향후 비즈니스 정책 변경 시, triggerUserId == receiverId 조건으로 필터링 필요

        // 알림 객체 생성(triggerNickname의 경우 연관관계 맺을때 추가할 예정
        Notification notification = Notification.forLike(reviewId, receiverId, triggerUserId,
            triggerNickname);

        // Todo 생성된 mapper로 return해줘야한다. (지금은 추상화 단계)
        // return notificationMapper.toDto(notificationRepository.save(notification));
        return notificationMapper.toDto(notificationRepsitory.save(notification));
    }

    /**
     * 내가 작성한 리뷰에 다른 사용자가 댓글을 남기면 알림을 생성한다.
     *
     * @param reviewId      알림 대상 리뷰 ID
     * @param triggerUserId 댓글을 작성한 사용자 ID
     * @param receiverId    알림을 받을 사용자 ID (리뷰 작성자)
     * @return 생성된 알림 객체
     */
    @Override
    @Transactional
    public NotificationDto createNotifyByComment(UUID reviewId, UUID triggerUserId,
        UUID receiverId) {
        // 리팩터링 대상: 실제 닉네임, 댓글 내용 포함 가능
        // Todo 실제 triggerNickname,comment, receiverid는 각각 review, User에서 조회할수 있어야 된다(리팩토링)
        String triggerNickname = "buzz"; // 임시
        String commentContent = "댓글 내용입니다"; // 임시

        // Todo
        // 현재는 자기 자신에게 좋아요/댓글을 남겨도 알림이 생성되도록 설계되어 있음.
        // 향후 비즈니스 정책 변경 시, triggerUserId == receiverId 조건으로 필터링 필요

        // 알림 객체 생성
        Notification notification = Notification.forComment(
            reviewId,
            receiverId,
            triggerUserId,
            triggerNickname,
            commentContent
        );

        // Todo 생성된 mapper로 return해줘야한다. (지금은 추상화 단계)
        // return notificationMapper.toDto(notificationRepository.save(notification));
        return notificationMapper.toDto(notificationRepsitory.save(notification));
    }


    /**
     * 알림의 확인(읽음) 상태를 업데이트합니다.
     *
     * @param notificationId 읽음 상태를 변경할 알림의 ID
     * @param receiverId     현재 요청을 보낸 사용자(알림 수신자)의 ID
     * @param confirmed      읽음 상태(true면 확인 처리)
     * @return 읽음 처리된 알림의 DTO 정보
     * @throws NotificationNotFoundException     알림이 존재하지 않는 경우
     * @throws NotificationAccessDeniedException 알림의 수신자와 요청자가 일치하지 않는 경우
     */
    @Override
    @Transactional
    public NotificationDto updateConfirmedStatus(UUID notificationId, UUID receiverId,
        boolean confirmed) {
        // 1. 알림 ID로 알림을 조회한다. 없으면 404 예외 발생
        Notification notification = notificationRepsitory.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        // 2. 알림의 수신자가 현재 요청자와 다를 경우 접근 권한 없음 예외 발생
        if (!notification.getReceiverId().equals(receiverId)) {
            throw new NotificationAccessDeniedException(receiverId, notificationId);
        }

        // 3. 요청값이 true인 경우에만 확인 처리 (추후 false 처리 허용 시 확장 가능)
        if (confirmed) {
            notification.markAsConfirmed();
        }

        // 4. 업데이트된 알림 정보를 DTO로 변환하여 반환
        return notificationMapper.toDto(notification);
    }

    /**
     * 사용자의 모든 알림을 읽음 상태로 일괄 처리합니다.
     *
     * @param receiverId 알림을 읽음 처리할 사용자 ID
     */
    @Override
    @Transactional
    public void updateAllConfirmedStatus(UUID receiverId) {
        // 1. 수신자 ID로 등록된 모든 알림을 조회
        List<Notification> notifications = notificationRepsitory.findAllByReceiverId(receiverId);

        // 2. 이미 읽지 않은 알림만 선별하여 확인 처리
        notifications.stream()
            .filter(n -> !n.isConfirmed())
            .forEach(Notification::markAsConfirmed); // JPA dirty checking
    }

}
