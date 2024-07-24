package com.Sadetechno.comment_module.Service;
import com.Sadetechno.comment_module.DTO.CommentRequest;
import com.Sadetechno.comment_module.DTO.CommentResponse;
import com.Sadetechno.comment_module.Repository.CommentRepository;
import com.Sadetechno.comment_module.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public CommentResponse createComment(CommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(request.getUserId());
        comment.setRepliedToUserId(request.getRepliedToUserId());
        comment.setTextContent(request.getTextContent());
        comment.setCreatedAt(LocalDateTime.now());

        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getRepliesByParentId(Long parentId) {
        List<Comment> replies = commentRepository.findByParentCommentId(parentId);
        return replies.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }


    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setUserId(comment.getUserId());
        response.setRepliedToUserId(comment.getRepliedToUserId());
        response.setTextContent(comment.getTextContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setReplies(comment.getReplies().stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toSet()));
        return response;
    }
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}