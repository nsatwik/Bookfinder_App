package com.bookfinder.repository;

import com.bookfinder.model.Admin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
  Optional<Admin> findByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
  boolean existsByUsernameIgnoreCase(String username);
  boolean existsByEmailIgnoreCase(String email);
}
