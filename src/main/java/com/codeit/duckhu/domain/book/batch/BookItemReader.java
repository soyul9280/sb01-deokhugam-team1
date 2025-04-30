package com.codeit.duckhu.domain.book.batch;

import com.codeit.duckhu.domain.book.entity.Book;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class BookItemReader implements ItemStreamReader<Book> {

    private final JpaPagingItemReader<Book> delegate;

    @Autowired
    public BookItemReader(
        EntityManagerFactory emf,
        @Value("#{jobParameters['from']}") Instant from,
        @Value("#{jobParameters['to']}")   Instant to
    ) {
        Map<String,Object> params = new HashMap<>();
        params.put("from", from);
        params.put("to",   to);

        // builder 로 딱 한 번에 페이징 리더 생성
        this.delegate = new JpaPagingItemReaderBuilder<Book>()
            .name("bookItemReader")
            .entityManagerFactory(emf)
            .pageSize(100)
            .queryString(
                "SELECT DISTINCT b " +
                    "FROM Book b JOIN b.reviews r " +
                    "WHERE r.createdAt BETWEEN :from AND :to"
            )
            .parameterValues(params)
            .build();  // 내부적으로 afterPropertiesSet() 까지 처리
    }

    // 만약 추가 초기화가 필요하면 @PostConstruct 에서도 호출할 수 있습니다.
    @PostConstruct
    public void init() throws Exception {
        delegate.afterPropertiesSet();
    }

    //해당 보일러패턴 @Delegate(excludes = Closeable.class)를 사용하여 줄이는것이 가능하다고 한다
    @Override
    public Book read() throws Exception {
        return delegate.read();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}
