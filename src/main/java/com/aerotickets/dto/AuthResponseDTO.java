package com.aerotickets.dto;

public class AuthResponseDTO {
  private String token;
  private String fullName;
  private String email;

  public AuthResponseDTO(String token, String fullName, String email) {
    this.token = token;
    this.fullName = fullName;
    this.email = email;
  }

  public String getToken() { return token; }
  public String getFullName() { return fullName; }
  public String getEmail() { return email; }
}