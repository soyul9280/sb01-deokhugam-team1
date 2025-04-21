package com.codeit.duckhu.domain.notification.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 1주일 이상 지난 확인된 알림을 삭제한다.
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :cutoff")
    void deleteOldConfirmedNotifications(@Param("cutoff") Instant cutoff);
}
