package com.Sadetechno.jwt_module.Repository;


import com.Sadetechno.jwt_module.model.OtpEntity;
import com.Sadetechno.jwt_module.model.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {

    Optional<OurUsers> findByEmail(String email);
    List<OurUsers> findAllByOrderByIdDesc();



}