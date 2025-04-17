package com.codeit.duckhu.review.repository;

import com.codeit.duckhu.review.entity.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,UUID> {

}
