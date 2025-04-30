package com.codeit.duckhu.domain.book.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import org.springframework.batch.item.ExecutionContext;
import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** JpaPagingItemReader<Book> 가 실제로 페이징해서 주어진 기간 내 리뷰가 있는 도서만 읽어오는지 검증합니다. */
@SpringBatchTest
@SpringBootTest(
    properties = "spring.main.allow-bean-definition-overriding=true"
)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class BookItemReaderTest {

    @Autowired DataSource dataSource;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired BookRepository bookRepository;
    @Autowired ReviewRepository reviewRepository;
    @Autowired EntityManagerFactory emf;

    private Instant from;
    private Instant to;
    private Book inPeriodBook;
    private BookItemReader reader;

    @BeforeEach
    void setUp() throws Exception {
        // 1) 배치 메타테이블
        new ResourceDatabasePopulator(
            new ClassPathResource("org/springframework/batch/core/schema-h2.sql")
        ).execute(dataSource);

        // 2) 기간 세팅
        to   = Instant.parse("2025-04-30T00:00:00Z");
        from = to.minus(1, ChronoUnit.DAYS);

        // 3) DB 초기화
        reviewRepository.deleteAll();
        bookRepository.deleteAll();

        // 4) 테스트용 도서·리뷰 준비 (생략; 기존 코드 동일)

        // 5) Reader 인스턴스 직접 생성
        reader = new BookItemReader(emf, from, to);
        reader.init();  // @PostConstruct 대체 또는 직접 afterPropertiesSet()
    }

    @Test
    void readOnlyBooksWithReviewsInPeriod() throws Exception {
        ExecutionContext ctx = new ExecutionContext();
        reader.open(ctx);

        List<Book> items = new ArrayList<>();
        Book b;
        while ((b = reader.read()) != null) {
            items.add(b);
        }
        reader.close();

        // inPeriodBook만 나와야 한다
        assertThat(items)
            .extracting(Book::getId)
            .containsExactly(inPeriodBook.getId());
    }
}
