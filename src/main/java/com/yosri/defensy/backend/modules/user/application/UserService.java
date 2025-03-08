package com.yosri.defensy.backend.modules.user.application;

import com.yosri.defensy.backend.modules.user.infrastructure.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(String id);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(String id, UserDto userDto);
    void deleteUserById(String id);
}
