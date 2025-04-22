package com.codeit.duckhu.domain.book.mapper;

import com.codeit.duckhu.domain.book.dto.BookDto;
import com.codeit.duckhu.domain.book.entity.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-22T13:14:21+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Amazon.com Inc.)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto toDto(Book book, Integer reviewCount, Double rating) {
        if ( book == null && reviewCount == null && rating == null ) {
            return null;
        }

        UUID id = null;
        String title = null;
        String author = null;
        String description = null;
        String publisher = null;
        LocalDate publishedDate = null;
        String isbn = null;
        String thumbnailUrl = null;
        Instant createdAt = null;
        Instant updatedAt = null;
        if ( book != null ) {
            id = book.getId();
            title = book.getTitle();
            author = book.getAuthor();
            description = book.getDescription();
            publisher = book.getPublisher();
            publishedDate = book.getPublishedDate();
            isbn = book.getIsbn();
            thumbnailUrl = book.getThumbnailUrl();
            createdAt = book.getCreatedAt();
            updatedAt = book.getUpdatedAt();
        }
        Integer reviewCount1 = null;
        reviewCount1 = reviewCount;
        Double rating1 = null;
        rating1 = rating;

        BookDto bookDto = new BookDto( id, title, author, description, publisher, publishedDate, isbn, thumbnailUrl, reviewCount1, rating1, createdAt, updatedAt );

        return bookDto;
    }
}
