package com.yosri.defensy.backend.modules.user.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "\\w{3,20}", message = "Username must be 3-20 characters long and contain only letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password; // Store as a **hashed value**

    private UserRole role; // ENUM for user roles

    private String profilePicture; // URL to profile image

    @Pattern(regexp = "\\d{10,15}", message = "Phone number must be 10-15 digits long")
    private String phoneNumber;

    private String address;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private Boolean isActive = true; // Soft delete

    private Instant lastLogin;
}
