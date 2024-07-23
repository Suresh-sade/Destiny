package com.Sadetechno.like_module.Service;

import com.Sadetechno.like_module.Repository.LikeRepository;
import com.Sadetechno.like_module.model.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    public Like toggleLike(Long postId, Long userId) {
        // Check if the user has already liked the post
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            // If a like exists, remove it
            likeRepository.delete(existingLike.get());
            return null; // Indicate that the like was removed
        } else {
            // If no like exists, add a new like
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            return likeRepository.save(like);
        }
    }

    public List<Like> getLikesByPostId(Long postId) {
        return likeRepository.findByPostId(postId);
    }
    public long getLikeCountByPostId(Long postId) {
        return likeRepository.countByPostId(postId);
    }
    public List<Long> getUserIdsWhoLikedPost(Long postId) {
        return likeRepository.findUserIdsByPostId(postId);
    }
}
