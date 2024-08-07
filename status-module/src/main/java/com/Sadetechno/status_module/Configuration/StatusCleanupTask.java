package com.Sadetechno.status_module.Configuration;

import com.Sadetechno.status_module.Repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StatusCleanupTask {
    @Autowired
    private StatusRepository statusRepository;
    @Scheduled(fixedRate = 3600000)
    public void  deleteOldStatuses(){
        LocalDateTime cutoff=LocalDateTime.now().minusHours(24);
        statusRepository.deleteByCreatedAtBefore(cutoff);
    }
}
