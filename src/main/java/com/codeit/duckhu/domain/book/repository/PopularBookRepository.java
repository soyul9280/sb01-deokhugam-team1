package com.codeit.duckhu.domain.book.repository;

import com.codeit.duckhu.domain.book.entity.PopularBook;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {}
