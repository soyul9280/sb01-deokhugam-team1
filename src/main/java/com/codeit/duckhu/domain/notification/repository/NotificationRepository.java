package com.codeit.duckhu.domain.notification.repository;

import com.codeit.duckhu.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  /** 특정 사용자가 수신한 모든 알림을 조회한다. */
  List<Notification> findAllByReceiverId(UUID receiverId);

  /** 1주일 이상 지난 확인된 알림을 삭제한다. */
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :cutoff")
  void deleteOldConfirmedNotifications(@Param("cutoff") Instant cutoff);
}
