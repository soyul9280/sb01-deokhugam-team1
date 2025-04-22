package com.codeit.duckhu.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 2092672286L;

    public static final QUser user = new QUser("user");

    public final com.codeit.duckhu.global.entity.QBaseEntity _super = new com.codeit.duckhu.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final BooleanPath deleted = createBoolean("deleted");

    public final StringPath email = createString("email");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final ListPath<com.codeit.duckhu.domain.review.entity.Review, com.codeit.duckhu.domain.review.entity.QReview> review = this.<com.codeit.duckhu.domain.review.entity.Review, com.codeit.duckhu.domain.review.entity.QReview>createList("review", com.codeit.duckhu.domain.review.entity.Review.class, com.codeit.duckhu.domain.review.entity.QReview.class, PathInits.DIRECT2);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

