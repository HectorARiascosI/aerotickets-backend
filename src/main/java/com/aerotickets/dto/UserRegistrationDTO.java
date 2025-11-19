package com.aerotickets.dto;

import com.aerotickets.constants.DtoValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {

    @NotBlank
    @Size(
            min = DtoValidationConstants.USER_FULLNAME_MIN,
            max = DtoValidationConstants.USER_FULLNAME_MAX
    )
    @Pattern(
            regexp = DtoValidationConstants.USER_FULLNAME_REGEX,
            message = DtoValidationConstants.USER_FULLNAME_MESSAGE
    )
    private String fullName;

    @NotBlank
    @Email
    @Size(max = DtoValidationConstants.USER_EMAIL_MAX)
    private String email;

    @NotBlank
    @Size(
            min = DtoValidationConstants.USER_PASSWORD_MIN,
            max = DtoValidationConstants.USER_PASSWORD_MAX
    )
    private String password;

    public String getFullName() {
        return fullName != null ? fullName.trim() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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