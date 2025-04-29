package com.codeit.duckhu.domain.book.batch;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import com.codeit.duckhu.domain.book.entity.Book;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookScoreProcessorTest {

    private final BookScoreProcessor processor = new BookScoreProcessor();

    @Test
    @DisplayName("책이 리뷰와 별점이 있다면 옳바르게 계산한다")
    void BookHasReviewsAndRating_thenCalculateScoreCorrectly() throws Exception {
        // given
        // score = 20 * 0.4 + 3.5 * 0.6 = 8.0 + 2.1 = 10.1)
        Book book =
            Book.builder()
                .title("Test Book")
                .author("Auth")
                .description("Desc")
                .publisher("Pub")
                .publishedDate(LocalDate.now().minusDays(1))
                .isbn("ISBN-1234")
                .reviewCount(20)
                .rating(3.5)
                .build();

        // when
        PopularBookScore result = processor.process(book);

        // then
        assertThat(result).isNotNull();
        assertThat(result.book()).isSameAs(book);
        assertThat(result.reviewCount()).isEqualTo(20);
        assertThat(result.rating()).isEqualTo(3.5);
        assertThat(result.score()).isCloseTo(10.1, offset(0.01));
    }

    @Test
    @DisplayName("책에 리뷰가 없으면 null을 반환한다.")
    void BookHasNoReviews_thenReturnNull() throws Exception {
        // given
        Book book =
            Book.builder()
                .title("No Reviews Book")
                .author("Auth")
                .description("Desc")
                .publisher("Pub")
                .publishedDate(LocalDate.now().minusDays(1))
                .isbn("ISBN-5678")
                .reviewCount(0)
                .rating(4.0)
                .build();

        // when
        PopularBookScore result = processor.process(book);

        // then
        assertThat(result).isNull();
    }
}

