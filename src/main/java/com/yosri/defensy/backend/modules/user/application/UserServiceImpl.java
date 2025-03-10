package com.yosri.defensy.backend.modules.user.application;

import com.yosri.defensy.backend.modules.user.domain.User;
import com.yosri.defensy.backend.modules.user.domain.UserRepository;
import com.yosri.defensy.backend.modules.user.domain.UserRole;
import com.yosri.defensy.backend.modules.user.events.*;
import com.yosri.defensy.backend.modules.user.infrastructure.UserDto;
import com.yosri.defensy.backend.modules.user.infrastructure.UserMapper;
import com.yosri.defensy.backend.modules.user.exceptions.UserAlreadyExistsException;
import com.yosri.defensy.backend.modules.user.exceptions.UserNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final String USER_NOT_FOUND = "User with ID %s not found.";
    private static final String DUPLICATE_USER_MESSAGE = "Username or Email is already registered.";

    public UserServiceImpl(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

@Override
public List<UserDto> getAllUsers() {

    List<UserDto> users= userRepository.findAll().stream()
            .map(UserMapper::toDto)
            .toList();// Simpler and more efficient

            // Publish UserRetrievedAllEvent when all users are fetched
        eventPublisher.publishEvent(new UserRetrievedAllEvent());
return users;

}

    @Override
    public UserDto getUserById(String id) {
        UserDto user = userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, id)));

                // Publish UserRetrievedEvent when a single user is fetched
        eventPublisher.publishEvent(new UserRetrievedEvent(id));
return user;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername()) || userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException(DUPLICATE_USER_MESSAGE);
        }

        if (userDto.getRole() == null) {
            userDto.setRole(UserRole.USER.name());
        }

        User user = UserMapper.toEntity(userDto);
        user = userRepository.save(user);

        eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), user.getUsername(), user.getEmail()));

        return UserMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, id)));

        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(UserRole.valueOf(userDto.getRole()));
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setIsActive(userDto.getIsActive());

        userRepository.save(existingUser);

        eventPublisher.publishEvent(new UserUpdatedEvent(existingUser.getId(), existingUser.getUsername(),
                existingUser.getEmail(), existingUser.getIsActive()));

        return UserMapper.toDto(existingUser);
    }

    @Override
    public void deleteUserById(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(String.format(USER_NOT_FOUND, id));
        }
        userRepository.deleteById(id);

        eventPublisher.publishEvent(new UserDeletedEvent(id));
    }
}
