package com.codeit.duckhu.domain.notification.service.impl;

import com.codeit.duckhu.domain.comment.domain.Comment;
import com.codeit.duckhu.domain.comment.exception.NoCommentException;
import com.codeit.duckhu.domain.comment.repository.CommentRepository;
import com.codeit.duckhu.domain.comment.service.CommentService;
import com.codeit.duckhu.domain.comment.service.ErrorCode;
import com.codeit.duckhu.domain.notification.dto.NotificationDto;
import com.codeit.duckhu.domain.notification.exception.NotificationAccessDeniedException;
import com.codeit.duckhu.domain.notification.exception.NotificationNotFoundException;
import com.codeit.duckhu.domain.notification.mapper.NotificationMapper;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.ReviewService;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.NotFoundUserException;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.domain.user.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.duckhu.domain.notification.entity.Notification;
import com.codeit.duckhu.domain.notification.repository.NotificationRepository;
import com.codeit.duckhu.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * 내가 작성한 리뷰에 다른 사용자가 좋아요를 누르면 알림을 생성한다.
     *
     * @param reviewId      알림 대상 리뷰 ID
     * @param triggerUserId 좋아요를 누른 사용자 ID
     * @return 생성된 알림 객체
     */
    @Override
    @Transactional
    public NotificationDto createNotifyByLike(UUID reviewId, UUID triggerUserId) {
        // 1. 리뷰 조회 → 수신자 ID 확보
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoSuchElementException());

        // 2. 수신자 ID = 리뷰 작성자의 ID
        UUID receiverId = review.getUser().getId();

        // 3. 트리거 유저 정보 → 닉네임 조회
        User triggerUser = userRepository.findById(triggerUserId)
            .orElseThrow(() -> new NotFoundUserException(triggerUserId));
        String nickname = triggerUser.getNickname();

        // Todo
        // 현재는 자기 자신에게 좋아요/댓글을 남겨도 알림이 생성되도록 설계되어 있음.
        // 향후 비즈니스 정책 변경 시, triggerUserId == receiverId 조건으로 필터링 필요

        // 4. 알림 생성
        Notification notification = Notification.forLike(
            reviewId,
            receiverId,
            nickname,
            review.getContent()
        );
        Notification saved = notificationRepository.save(notification);

        // DTO 생성
        return notificationMapper.toDto(saved);
    }

    /**
     * 내가 작성한 리뷰에 다른 사용자가 댓글을 남기면 알림을 생성한다.
     *
     * @param reviewId      알림 대상 리뷰 ID
     * @param triggerUserId 댓글을 작성한 사용자 ID
     * @param comment       댓글 내용
     * @return 생성된 알림 객체
     */
    @Override
    @Transactional
    public NotificationDto createNotifyByComment(UUID reviewId, UUID triggerUserId,
        String comment) {
        // 1. 리뷰 조회 → 알림 수신자 확인 + 리뷰 제목 확보
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NoCommentException(ErrorCode.NOT_FOUND_COMMENT));

        // 2. 수신자 ID = 리뷰 작성자의 ID
        UUID receiverId = review.getUser().getId();

        // 3. 댓글 작성자 정보 -> 닉네임
        User triggerUser = userRepository.findById(receiverId)
            .orElseThrow(() -> new NotFoundUserException(receiverId));
        String nickname = triggerUser.getNickname();

        // 알림 객체 생성
        Notification notification = Notification.forComment(
            reviewId,
            receiverId,
            nickname,
            comment,
            review.getContent()
        );

        return notificationMapper.toDto(notificationRepository.save(notification));
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
        Notification notification = notificationRepository.findById(notificationId)
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
        List<Notification> notifications = notificationRepository.findAllByReceiverId(receiverId);

        // 2. 이미 읽지 않은 알림만 선별하여 확인 처리
        notifications.stream()
            .filter(n -> !n.isConfirmed())
            .forEach(Notification::markAsConfirmed); // JPA dirty checking
    }

    @Override
    @Transactional
    public void deleteConfirmedNotificationsOlderThanAWeek() {
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        notificationRepository.deleteOldConfirmedNotifications(cutoff);
    }
}
