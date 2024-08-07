package com.Sadetechno.status_module.Controller;
import com.Sadetechno.status_module.Service.StatusService;
import com.Sadetechno.status_module.model.Privacy;
import com.Sadetechno.status_module.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/statuses")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping("/post")
    public ResponseEntity<Status> postStatus(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("duration") int duration,
            @RequestParam("privacy") Privacy privacy,
            @RequestParam("userId") Long userId) throws IOException {

        Status status = statusService.saveStatus(file, type, duration, privacy, userId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Status>> getStatusesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(statusService.getStatusesByUserId(userId));
    }
}


