package com.codeit.duckhu.domain.notification.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeit.duckhu.domain.notification.entity.Notification;

@Repository
public interface NotificationRepsitory extends JpaRepository<Notification, UUID> {
	Notification save(Notification notification);
}
