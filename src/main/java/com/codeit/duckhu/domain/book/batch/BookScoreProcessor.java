package com.codeit.duckhu.domain.book.batch;

import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import com.codeit.duckhu.domain.book.entity.Book;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class BookScoreProcessor implements ItemProcessor<Book, PopularBookScore> {

    private static final double REVIEW_WEIGHT = 0.4;
    private static final double RATING_WEIGHT = 0.6;

    @Override
    public PopularBookScore process(Book book) throws Exception {
        if (book == null || book.getReviewCount() <= 0) {
            return null;
        }

        int reviewCount = book.getReviewCount();
        double rating = book.getRating();
        double score = calculateScore(reviewCount, rating);

        return new PopularBookScore(book, reviewCount, rating, score);
    }

    private double calculateScore(int reviewCount, double rating) {
        return (reviewCount * REVIEW_WEIGHT) + (rating * RATING_WEIGHT);
    }
}