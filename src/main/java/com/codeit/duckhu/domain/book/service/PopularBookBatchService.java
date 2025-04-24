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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    log.info("[Batch 시작] period={} | from={} ~ to={}", period, from, now);

    List<Book> books = bookRepository.findAll();

    List<PopularBookScore> popularBookScores = books.stream()
        .map(book -> {
          int reviewCount = reviewRepository.countByBookIdAndCreatedAtBetween(book.getId(), from, now);
          double rating = reviewRepository.calculateAverageRatingByBookIdAndCreatedAtBetween(book.getId(), from, now);
          double score = (reviewCount * 0.4) + (rating * 0.6);

          log.info("도서: {} | 리뷰 수: {} | 평점: {} | 점수: {}", book.getTitle(), reviewCount, rating, score);

          return new PopularBookScore(book, reviewCount, rating, score);
        })
        .filter(sb -> sb.reviewCount() > 0)
        .sorted(Comparator.comparingDouble(PopularBookScore::score).reversed())
        .toList();

    popularBookRepository.deleteByPeriod(period);
    log.info("[삭제 완료] 기존 PopularBook 삭제 - period={}", period);

    List<PopularBook> popularBooks = new ArrayList<>();
    for (int i = 0; i < popularBookScores.size(); i++) {
      PopularBookScore pb = popularBookScores.get(i);

      log.info("[랭킹 {}] 도서: {} | 리뷰 수: {} | 평점: {} | 점수: {}", i + 1,
          pb.book().getTitle(), pb.reviewCount(), pb.rating(), pb.score());

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
    log.info("[저장 완료] PopularBook {}건 저장 완료", popularBooks.size());
  }
}
