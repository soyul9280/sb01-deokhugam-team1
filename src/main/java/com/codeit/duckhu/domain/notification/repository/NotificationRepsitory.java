package com.codeit.duckhu.domain.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeit.duckhu.domain.notification.entity.Notification;

@Repository
public interface NotificationRepsitory extends JpaRepository<Notification, UUID> {


    /**
     * 특정 사용자가 수신한 모든 알림을 조회합니다.
     *
     * @param receiverId 알림 수신자 ID
     * @return 해당 사용자의 알림 리스트
     */
    List<Notification> findAllByReceiverId(UUID receiverId);

    /**
     * 특정 사용자의 아직 읽지 않은(confirmed = false) 알림만 조회합니다.
     *
     * @param receiverId 알림 수신자 ID
     * @return 미확인 알림 리스트
     */
    List<Notification> findAllByReceiverIdAndConfirmedIsFalse(UUID receiverId);
}
