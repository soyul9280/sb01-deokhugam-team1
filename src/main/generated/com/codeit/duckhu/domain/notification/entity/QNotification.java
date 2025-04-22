package com.codeit.duckhu.domain.notification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QNotification is a Querydsl query type for Notification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotification extends EntityPathBase<Notification> {

    private static final long serialVersionUID = 1618620766L;

    public static final QNotification notification = new QNotification("notification");

    public final com.codeit.duckhu.global.entity.QBaseUpdatableEntity _super = new com.codeit.duckhu.global.entity.QBaseUpdatableEntity(this);

    public final BooleanPath confirmed = createBoolean("confirmed");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final ComparablePath<java.util.UUID> receiverId = createComparable("receiverId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> reviewId = createComparable("reviewId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> triggerUserId = createComparable("triggerUserId", java.util.UUID.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public QNotification(String variable) {
        super(Notification.class, forVariable(variable));
    }

    public QNotification(Path<? extends Notification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNotification(PathMetadata metadata) {
        super(Notification.class, metadata);
    }

}

