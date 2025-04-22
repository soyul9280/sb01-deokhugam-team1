package com.codeit.duckhu.domain.notification.entity;

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

	@Column(name = "review_title", length = 255)
	private String reviewTitle;

	@Column(name = "content", length = 100, nullable = false)
	private String content;

	@Column(name = "confirmed", nullable = false)
	private boolean confirmed = false;

	private Notification(UUID reviewId, UUID receiverId, UUID triggerUserId, String content, String reviewTitle) {
		this.reviewId = reviewId;
		this.receiverId = receiverId;
		this.triggerUserId = triggerUserId;
		this.content = content;
		this.reviewTitle = reviewTitle;
		this.confirmed = false;
	}

	public static Notification forLike(UUID reviewId, UUID receiverId, UUID triggerUserId, String nickname, String reviewTitle) {
		String content = String.format("[%s]님이 나의 리뷰를 좋아합니다.", nickname);
		return new Notification(reviewId, receiverId, triggerUserId, content, reviewTitle);
	}

	public static Notification forComment(UUID reviewId, UUID receiverId, UUID triggerUserId, String nickname, String comment, String reviewTitle) {
		String content = String.format("[%s]님이 나의 리뷰에 댓글을 남겼습니다.\n%s", nickname, comment);
		return new Notification(reviewId, receiverId, triggerUserId, content, reviewTitle);
	}

	public void markAsConfirmed() {
		this.confirmed = true;
	}
}
