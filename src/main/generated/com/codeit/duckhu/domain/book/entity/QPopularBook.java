package com.codeit.duckhu.domain.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPopularBook is a Querydsl query type for PopularBook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPopularBook extends EntityPathBase<PopularBook> {

    private static final long serialVersionUID = 2147463377L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPopularBook popularBook = new QPopularBook("popularBook");

    public final com.codeit.duckhu.global.entity.QBaseEntity _super = new com.codeit.duckhu.global.entity.QBaseEntity(this);

    public final QBook book;

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final EnumPath<com.codeit.duckhu.global.type.PeriodType> period = createEnum("period", com.codeit.duckhu.global.type.PeriodType.class);

    public final NumberPath<Integer> rank = createNumber("rank", Integer.class);

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final NumberPath<Double> score = createNumber("score", Double.class);

    public QPopularBook(String variable) {
        this(PopularBook.class, forVariable(variable), INITS);
    }

    public QPopularBook(Path<? extends PopularBook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPopularBook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPopularBook(PathMetadata metadata, PathInits inits) {
        this(PopularBook.class, metadata, inits);
    }

    public QPopularBook(Class<? extends PopularBook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.book = inits.isInitialized("book") ? new QBook(forProperty("book")) : null;
    }

}

