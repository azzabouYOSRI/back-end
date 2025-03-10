package com.yosri.defensy.backend.modules.user.infrastructure;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "\\w{3,20}", message = "Username must be 3-20 characters long and contain only letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String role;

    @Pattern(regexp = "\\d{10,15}", message = "Phone number must be 10-15 digits long")
    private String phoneNumber;

    private String address;

    private Boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLogin;
}
