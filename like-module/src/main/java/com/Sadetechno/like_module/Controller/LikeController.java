package com.Sadetechno.like_module.Controller;
import com.Sadetechno.like_module.Service.LikeService;
import com.Sadetechno.like_module.model.Like;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(
            @RequestParam("postId") Long postId,
            @RequestParam("userId") Long userId) {

        Like like = likeService.toggleLike(postId, userId);
        if (like == null) {
            return new ResponseEntity<>("Like removed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(like, HttpStatus.CREATED);
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Like>> getLikesByPostId(@PathVariable Long postId) {
        List<Like> likes = likeService.getLikesByPostId(postId);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getLikeCountByPostId(@PathVariable Long postId) {
        long count = likeService.getLikeCountByPostId(postId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
    @GetMapping("/post/{postId}/users")
    public ResponseEntity<List<Long>> getUsersWhoLikedPost(@PathVariable Long postId) {
        List<Long> userIds = likeService.getUserIdsWhoLikedPost(postId);
        return new ResponseEntity<>(userIds, HttpStatus.OK);
    }
}


