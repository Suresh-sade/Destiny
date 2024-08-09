package com.Sadetechno.post_module.Service;
import com.Sadetechno.post_module.Repository.PostRepository;
import com.Sadetechno.post_module.model.Post;
import com.Sadetechno.post_module.model.PostType;
import com.Sadetechno.post_module.model.PrivacySetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private FileUploadService fileUploadService; // Injecting FileUploadService

    public Post createPost(Long userId, String postType, String textContent, MultipartFile imageFile, MultipartFile videoFile, String description, String privacySetting) throws IOException {
        Post post = new Post();
        PostType type = PostType.valueOf(postType);
        post.setPostType(type);
        post.setDescription(description);
        post.setUserId(userId);

        PrivacySetting privacy = PrivacySetting.valueOf(privacySetting.toUpperCase());
        post.setPrivacySetting(privacy);  // Set privacy setting

        switch (type) {
            case TEXT:
                post.setTextContent(textContent);
                break;
            case IMAGE:
                if (imageFile != null && !imageFile.isEmpty()) {
                    String imageUrl = fileUploadService.uploadFile(imageFile);
                    post.setImageUrl(imageUrl);
                } else {
                    throw new IllegalArgumentException("Image file is required for IMAGE post type.");
                }
                break;
            case VIDEO:
                if (videoFile != null && !videoFile.isEmpty()) {
                    String videoUrl = fileUploadService.uploadFile(videoFile);
                    post.setVideoUrl(videoUrl);
                } else {
                    throw new IllegalArgumentException("Video file is required for VIDEO post type.");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid post type");
        }

        return postRepository.save(post);
    }
    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void deletePost(Long postId) throws IOException {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null) {
            // Delete associated files if they exist
            if (post.getImageUrl() != null) {
                fileUploadService.deleteFile(post.getImageUrl());
            }
            if (post.getVideoUrl() != null) {
                fileUploadService.deleteFile(post.getVideoUrl());
            }
            postRepository.delete(post);
        }
    }
    public List<Post> getAllPost() {
        return postRepository.findAll();
    }
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }
}
