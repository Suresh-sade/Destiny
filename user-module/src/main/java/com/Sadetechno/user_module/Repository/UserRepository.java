package com.Sadetechno.user_module.Repository;

import com.Sadetechno.user_module.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

