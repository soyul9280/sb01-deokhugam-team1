package com.codeit.duckhu.domain.review.batch;

import com.codeit.duckhu.domain.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaPagingItemReader;

@RequiredArgsConstructor
public class PopularReviewItemReader extends JpaPagingItemReader<Review> {}
