package com.codeit.duckhu.domain.comments.repository;

import com.codeit.duckhu.domain.comments.domain.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
