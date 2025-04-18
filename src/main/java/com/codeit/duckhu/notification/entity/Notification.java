package com.codeit.duckhu.notification.entity;

import java.util.UUID;

import com.codeit.duckhu.global.entity.BaseEntity;
import com.codeit.duckhu.global.entity.BaseUpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseUpdatableEntity {

	@Column(name = "review_id", columnDefinition = "UUID")
	private UUID reviewId;

	@Column(name = "user_id", columnDefinition = "UUID")
	private UUID receiverId;

	@Column(name = "trigger_user_id", columnDefinition = "UUID")
	private UUID triggerUserId;

	@Column(name = "content", length = 100, nullable = false)
	private String content;

	@Column(name = "confirmed", nullable = false)
	private boolean confirmed = false;

	public Notification(UUID reviewId, UUID receiverId, UUID triggerUserId, String content) {
		this.reviewId = reviewId;
		this.receiverId = receiverId;
		this.triggerUserId = triggerUserId;
		this.content = content;
		this.confirmed = false;
	}
}
