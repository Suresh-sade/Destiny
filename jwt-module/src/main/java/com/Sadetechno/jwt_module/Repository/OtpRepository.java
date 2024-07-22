package com.Sadetechno.jwt_module.Repository;


import com.Sadetechno.jwt_module.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findByEmailAndOtp(String email, String otp);
    Optional<OtpEntity> findByEmail(String email);
}


