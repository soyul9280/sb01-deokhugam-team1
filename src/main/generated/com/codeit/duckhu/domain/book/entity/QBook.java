package com.codeit.duckhu.domain.book.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBook is a Querydsl query type for Book
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBook extends EntityPathBase<Book> {

    private static final long serialVersionUID = -1534687654L;

    public static final QBook book = new QBook("book");

    public final com.codeit.duckhu.global.entity.QBaseUpdatableEntity _super = new com.codeit.duckhu.global.entity.QBaseUpdatableEntity(this);

    public final StringPath author = createString("author");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    //inherited
    public final ComparablePath<java.util.UUID> id = _super.id;

    public final StringPath isbn = createString("isbn");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final ListPath<PopularBook, QPopularBook> popularBooks = this.<PopularBook, QPopularBook>createList("popularBooks", PopularBook.class, QPopularBook.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> publishedDate = createDate("publishedDate", java.time.LocalDate.class);

    public final StringPath publisher = createString("publisher");

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final ListPath<com.codeit.duckhu.domain.review.entity.Review, com.codeit.duckhu.domain.review.entity.QReview> reviews = this.<com.codeit.duckhu.domain.review.entity.Review, com.codeit.duckhu.domain.review.entity.QReview>createList("reviews", com.codeit.duckhu.domain.review.entity.Review.class, com.codeit.duckhu.domain.review.entity.QReview.class, PathInits.DIRECT2);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public QBook(String variable) {
        super(Book.class, forVariable(variable));
    }

    public QBook(Path<? extends Book> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBook(PathMetadata metadata) {
        super(Book.class, metadata);
    }

}

