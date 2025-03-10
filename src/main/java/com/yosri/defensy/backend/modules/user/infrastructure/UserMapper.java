package com.yosri.defensy.backend.modules.user.infrastructure;

import com.yosri.defensy.backend.modules.user.domain.User;
import com.yosri.defensy.backend.modules.user.domain.UserRole;

public class UserMapper {

    private UserMapper() {
    throw new UnsupportedOperationException("Utility class");
}

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    public static User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(UserRole.valueOf(userDto.getRole()))
                .phoneNumber(userDto.getPhoneNumber())
                .address(userDto.getAddress())
                .isActive(userDto.getIsActive())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .lastLogin(userDto.getLastLogin())
                .build();
    }
}
