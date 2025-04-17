package com.codeit.duckhu.notification.entity;

import java.util.UUID;

public class Notification {
	private UUID id;
	private UUID reviewId;
	private UUID receiverId;
	private UUID triggerUserId;
	private String content;

	public Notification(UUID id, UUID reviewId, UUID receiverId, UUID triggerUserId, String content) {
		this.id = id;
		this.reviewId = reviewId;
		this.receiverId = receiverId;
		this.triggerUserId = triggerUserId;
		this.content = content;
	}

	public UUID getId() {
		return id;
	}

	public UUID getReviewId() {
		return reviewId;
	}

	public UUID getReceiverId() {
		return receiverId;
	}

	public UUID getTriggerUserId() {
		return triggerUserId;
	}

	public String getContent() {
		return content;
	}
}
