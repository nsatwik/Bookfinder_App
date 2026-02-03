package com.bookfinder.dto;

public class AuthResponse {
  private final String message;
  private final String username;
  private final String role;

  public AuthResponse(String message, String username, String role) {
    this.message = message;
    this.username = username;
    this.role = role;
  }

  public String getMessage() {
    return message;
  }

  public String getUsername() {
    return username;
  }

  public String getRole() {
    return role;
  }
}
