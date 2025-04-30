package com.codeit.duckhu.domain.book.batch;

import com.codeit.duckhu.domain.book.dto.PopularBookScore;
import com.codeit.duckhu.domain.book.entity.PopularBook;
import com.codeit.duckhu.domain.book.repository.popular.PopularBookRepository;
import com.codeit.duckhu.global.type.PeriodType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
//@StepScope
//@RequiredArgsConstructor
//public class BookItemWriter implements ItemWriter<PopularBookScore> {
//
//    private final PopularBookRepository popularBookRepository;
//
//    @Value("#{jobParameters['period']}")
//    private PeriodType period;
//
//    @Override
//    public void write(List<? extends PopularBookScore> items) throws Exception {
//        if (items.isEmpty()) {
//            return;
//        }
//
//        // 이전에 저장된 각 기간의 랭킹 삭제
//        popularBookRepository.deleteByPeriod(period);
//
//        List<PopularBook> entities = new ArrayList<>(items.size());
//        for (int i = 0; i < items.size(); i++) {
//            PopularBookScore score = items.get(i);
//            PopularBook pb = PopularBook.builder()
//                .book(score.book())
//                .period(period)
//                .reviewCount(score.reviewCount())
//                .rating(score.rating())
//                .score(score.score())
//                .rank(i + 1)
//                .build();
//            entities.add(pb);
//        }
//
//        // 3) 한 번에 저장
//        popularBookRepository.saveAll(entities);
//    }
//}
