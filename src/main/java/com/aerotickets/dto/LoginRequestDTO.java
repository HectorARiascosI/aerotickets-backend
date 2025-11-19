package com.aerotickets.dto;

import com.aerotickets.constants.DtoValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {

    @NotBlank
    @Email
    @Size(max = DtoValidationConstants.LOGIN_EMAIL_MAX)
    private String email;

    @NotBlank
    @Size(
            min = DtoValidationConstants.LOGIN_PASSWORD_MIN,
            max = DtoValidationConstants.LOGIN_PASSWORD_MAX
    )
    private String password;

    public String getEmail() {
        return email != null ? email.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password != null ? password.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}