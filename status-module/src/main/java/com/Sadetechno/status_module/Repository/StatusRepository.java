package com.Sadetechno.status_module.Repository;

import com.Sadetechno.status_module.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status,Long> {
    List<Status> findByUserId(Long userId);
    List<Status> findByCreatedAtBefore(LocalDateTime cutoff);

}
