package com.codeit.duckhu.domain.review.repository;

import com.codeit.duckhu.domain.review.entity.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,UUID> {

}
