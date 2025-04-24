package com.codeit.duckhu.domain.notification.repository;

import com.codeit.duckhu.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
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

  // 전체 카운트
  long countByReceiverId(UUID receiverId);

  // DESC 정렬, 커서 없을 때
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.receiverId = :receiverId
     ORDER BY n.createdAt DESC
    """)
  List<Notification> findDescNoCursor(
      @Param("receiverId") UUID receiverId,
      Pageable pageable);

  // DESC 정렬, 커서 있을 때 (createdAt < :cursor)
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.receiverId = :receiverId
       AND n.createdAt < :cursor
     ORDER BY n.createdAt DESC
    """)
  List<Notification> findDescWithCursor(
      @Param("receiverId") UUID receiverId,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  // ASC 정렬, 커서 없을 때
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.receiverId = :receiverId
     ORDER BY n.createdAt ASC
    """)
  List<Notification> findAscNoCursor(
      @Param("receiverId") UUID receiverId,
      Pageable pageable);

  // ASC 정렬, 커서 있을 때 (createdAt > :cursor)
  @Query("""
    SELECT n
      FROM Notification n
     WHERE n.receiverId = :receiverId
       AND n.createdAt > :cursor
     ORDER BY n.createdAt ASC
    """)
  List<Notification> findAscWithCursor(
      @Param("receiverId") UUID receiverId,
      @Param("cursor") Instant cursor,
      Pageable pageable);
}
