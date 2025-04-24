package com.codeit.duckhu.domain.book.service;

import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.entity.PopularBook;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.book.repository.popular.PopularBookRepository;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PopularBookBatchService {

  private final BookRepository bookRepository;
  private final ReviewRepository reviewRepository;
  private final PopularBookRepository popularBookRepository;

  @Transactional
  public void savePopularBook(PeriodType period) {
    Instant now = Instant.now();
    Instant from = period.toStartInstant(now);

    List<Book> books = bookRepository.findAll();

    List<PopularBookScore> popularBookScores = books.stream()
        .map(book -> {
          int reviewCount = reviewRepository.countByBookIdAndCreatedAtBetween(book.getId(), from,
              now);
          double rating = reviewRepository.countByBookIdAndCreatedAtBetween(book.getId(), from,
              now);

          double score = (reviewCount * 0.4) + (rating * 0.6);

          return new PopularBookScore(book, reviewCount, rating, score);
        })
        .filter(sb -> sb.reviewCount() > 0)
        .sorted(Comparator.comparingDouble(PopularBookScore::score).reversed())
        .toList();

    popularBookRepository.deleteByPeriod(period);

    List<PopularBook> popularBooks = new ArrayList<>();
    for (int i = 0; i < popularBookScores.size(); i++) {
      PopularBookScore pb = popularBookScores.get(i);
      PopularBook popularBook = PopularBook.builder()
          .book(pb.book())
          .period(period)
          .reviewCount(pb.reviewCount())
          .rating(pb.rating())
          .score(pb.score())
          .rank(i + 1)
          .build();
      popularBooks.add(popularBook);
    }

    popularBookRepository.saveAll(popularBooks);
  }
}
