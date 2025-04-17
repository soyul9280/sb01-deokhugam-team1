package com.codeit.duckhu.notification.repository;

import org.springframework.stereotype.Repository;

import com.codeit.duckhu.notification.entity.Notification;

@Repository
public interface NotificationRepsitory {
	void save(Notification notification);
}
