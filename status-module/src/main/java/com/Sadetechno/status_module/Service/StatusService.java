package com.Sadetechno.status_module.Service;

import com.Sadetechno.status_module.Repository.StatusRepository;
import com.Sadetechno.status_module.model.Privacy;
import com.Sadetechno.status_module.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private FileUploadService fileUploadService;

    private static final int MAX_VIDEO_LENGTH = 50; // 50 seconds
    private static final int MAX_IMAGE_TEXT_LENGTH = 35; // 35 seconds

    public Status saveStatus(MultipartFile file, String type, int duration, Privacy privacy, Long userId) throws IOException {
        String filePath = fileUploadService.uploadFile(file);

        Status status = new Status();
        status.setContent(filePath);
        status.setType(type);
        status.setCreatedAt(LocalDateTime.now());
        status.setDuration(duration);
        status.setPrivacy(privacy);
        status.setUserId(userId);

        switch (type.toLowerCase()) {
            case "video":
                if (duration > MAX_VIDEO_LENGTH) {
                    throw new IllegalArgumentException("Video length cannot exceed 50 seconds");
                }
                break;
            case "image":
            case "text":
                if (duration > MAX_IMAGE_TEXT_LENGTH) {
                    throw new IllegalArgumentException("Image or text length cannot exceed 35 seconds");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid content type");
        }

        return statusRepository.save(status);
    }

    public List<Status> getStatusesByUserId(Long userId) {
        return statusRepository.findByUserId(userId);
    }
}
