package com.Sadetechno.like_module.Repository;


import com.Sadetechno.like_module.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPostId(Long postId);

    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
    @Query("SELECT l.userId FROM Like l WHERE l.postId = :postId")
    List<Long> findUserIdsByPostId(@Param("postId") Long postId);

}


