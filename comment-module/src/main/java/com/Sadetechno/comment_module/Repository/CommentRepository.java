package com.Sadetechno.comment_module.Repository;

import com.Sadetechno.comment_module.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByParentCommentId(Long parentId);
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);
}
