package com.bookfinder.service;

import com.bookfinder.dto.AuthResponse;
import com.bookfinder.dto.ForgotRequest;
import com.bookfinder.dto.LoginRequest;
import com.bookfinder.dto.RegisterRequest;
import com.bookfinder.model.Admin;
import com.bookfinder.model.Role;
import com.bookfinder.model.User;
import com.bookfinder.repository.AdminRepository;
import com.bookfinder.repository.UserRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public AuthService(UserRepository userRepository, AdminRepository adminRepository) {
    this.userRepository = userRepository;
    this.adminRepository = adminRepository;
  }

  public AuthResponse register(RegisterRequest request) {
    String username = request.getUsername().trim();
    String email = request.getEmail().trim();

    if (userRepository.existsByUsernameIgnoreCase(username) || adminRepository.existsByUsernameIgnoreCase(username)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use.");
    }
    if (userRepository.existsByEmailIgnoreCase(email) || adminRepository.existsByEmailIgnoreCase(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
    }

    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPasswordHash(encoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    userRepository.save(user);

    return new AuthResponse("Registration successful. Please sign in.", user.getUsername(), user.getRole().name());
  }

  public AuthResponse login(LoginRequest request) {
    String identifier = request.getUsernameOrEmail().trim();

    Optional<User> userMatch = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier);
    if (userMatch.isPresent()) {
      User user = userMatch.get();
      if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
      }
      return new AuthResponse("Welcome back, " + user.getUsername() + ".", user.getUsername(), user.getRole().name());
    }

    Optional<Admin> adminMatch = adminRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier);
    if (adminMatch.isPresent()) {
      Admin admin = adminMatch.get();
      if (!encoder.matches(request.getPassword(), admin.getPasswordHash())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
      }
      return new AuthResponse("Welcome back, " + admin.getUsername() + ".", admin.getUsername(), Role.ADMIN.name());
    }

    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
  }

  public AuthResponse forgot(ForgotRequest request) {
    return new AuthResponse(
        "If the email exists, reset instructions were sent.",
        request.getEmail(),
        ""
    );
  }
}
