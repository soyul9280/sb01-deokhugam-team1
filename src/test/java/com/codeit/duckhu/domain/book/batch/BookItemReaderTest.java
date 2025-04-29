package com.codeit.duckhu.domain.book.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.http.ExecutionContext;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/** JpaPagingItemReader<Book> 가 실제로 페이징해서 주어진 기간 내 리뷰가 있는 도서만 읽어오는지 검증합니다. */
@SpringBatchTest
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 인메모리 H2 데이터베이스 사용
public class BookItemReaderTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // 아직 구현 전이므로 컴파일 에러가 납니다!
    @Autowired
    private BookItemReader reader;

    private Instant from;
    private Instant to;
    private Book inPeriodBook;

    @BeforeEach
    void setUp() {
        // 1) 스프링 배치 메타테이블 스키마 로딩 (H2)
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            new ClassPathResource("org/springframework/batch/core/schema-h2.sql")
        );
        populator.execute(dataSource);

        // 테스트 기간 정하기
        // 2) 테스트용 기간: 2025-04-29 00:00:00Z ~ 2025-04-30 00:00:00Z
        to = Instant.parse("2025-04-30T00:00:00Z");
        from = to.minus(1, ChronoUnit.DAYS);

        // 3) 실행전 초기화
        reviewRepository.deleteAll();
        bookRepository.deleteAll();

        // 4) 책 생성
        inPeriodBook = bookRepository.save(
            Book.builder()
                .title("TDD 친절 가이드")
                .author("홍길동")
                .description("설명")
                .publisher("테스트출판사")
                .publishedDate(java.time.LocalDate.now())
                .isbn("ISBN-TEST-1")
                .build()
        );
        Book outPeriodBook = bookRepository.save(
            Book.builder()
                .title("오래된 책")
                .author("테스트")
                .description("오래된 설명")
                .publisher("출판사")
                .publishedDate(java.time.LocalDate.now().minusYears(1))
                .isbn("ISBN-OLD-1")
                .build()
        );
        Book noReviewBook = bookRepository.save(
            Book.builder()
                .title("리뷰 없는 책")
                .author("테스트")
                .description("없음")
                .publisher("출판사")
                .publishedDate(java.time.LocalDate.now())
                .isbn("ISBN-NONE-1")
                .build()
        );

        // 5) 리뷰 생성 & created_at 덮어쓰기
        Review inReview = Review.builder()
            .content("기간 내 리뷰")
            .rating(5)
            .book(inPeriodBook)
            .build();
        inReview = reviewRepository.save(inReview);
        // 바로 JDBC로 created_at을 2025-04-29T12:00:00Z로 강제 설정
        jdbcTemplate.update(
            "UPDATE reviews SET created_at = ? WHERE id = ?",
            Timestamp.from(Instant.parse("2025-04-29T12:00:00Z")),
            inReview.getId()
        );

        Review outReview = Review.builder()
            .content("기간 외 리뷰")
            .rating(3)
            .book(outPeriodBook)
            .build();
        outReview = reviewRepository.save(outReview);
        jdbcTemplate.update(
            "UPDATE reviews SET created_at = ? WHERE id = ?",
            Timestamp.from(Instant.parse("2025-04-28T08:00:00Z")),
            outReview.getId()
        );
    }

    @Test
    @DisplayName("from~to 기간 내 리뷰가 있는 책만 읽어야 한다")
    void readOnlyBooksWithReviewsInPeriod() throws Exception {
        ExecutionContext ctx = new ExecutionContext();
        StepScopeTestUtils.doInStepScope(
            ctx,
            () -> {
                reader.open(ctx);

                List<Book> items = new ArrayList<>();
                Book b;
                while ((b = reader.read()) != null) {
                    items.add(b);
                }

                // inPeriodBook만 읽혀야 한다
                assertThat(items)
                    .extracting(Book::getId)
                    .containsExactly(inPeriodBook.getId());

                reader.close();
                return null;
            },
            Collections.singletonMap("from", from),
            Collections.singletonMap("to", to)
        );
    }
}
