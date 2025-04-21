<<<<<<<< HEAD:src/main/java/com/codeit/duckhu/domain/comments/repository/CommentRepository.java
package com.codeit.duckhu.domain.comments.repository;

import com.codeit.duckhu.domain.comments.domain.Comment;
========
package com.codeit.duckhu.domain.comment.repository;

import com.codeit.duckhu.domain.comment.domain.Comment;
>>>>>>>> 131530772d8d3af4cc636abca04259c6c4f8bd51:src/main/java/com/codeit/duckhu/domain/comment/repository/CommentRepository.java
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
