package com.blogapp.api.services.impl;

import com.blogapp.api.dto.ChangePasswordRequest;
import com.blogapp.api.dto.UserDto;
import com.blogapp.api.models.UserEntity;
import com.blogapp.api.repository.UserRepository;
import com.blogapp.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    public List<UserDto> searchUsersByUsername(String username) {
        List<UserEntity> users = userRepository.findByUsernameContaining(username);
        return users.stream().map(this::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto getUserProfile(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        return userOptional.map(this::mapToUserDto).orElse(null);
    }

    public UserDto updateUser(Long userId, UserDto updatedUserDto) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            mapToUserEntity(updatedUserDto, user);
            userRepository.save(user);
            return mapToUserDto(user);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserDto mapToUserDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setProfilePictureUrl(user.getProfilePictureUrl());
        userDto.setCoverPictureUrl(user.getCoverPictureUrl());
        userDto.setBio(user.getBio());

        return userDto;
    }

    private void mapToUserEntity(UserDto userDto, UserEntity user) {
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setBio(userDto.getBio());

        // Update profile picture if present in the DTO
        String newProfilePictureUrl = userDto.getProfilePictureUrl();
        if (newProfilePictureUrl != null && !newProfilePictureUrl.isEmpty()) {
            user.setProfilePictureUrl(newProfilePictureUrl);
        }

        // Update cover picture if present in the DTO
        String newCoverPictureUrl = userDto.getCoverPictureUrl();
        if (newCoverPictureUrl != null && !newCoverPictureUrl.isEmpty()) {
            user.setCoverPictureUrl(newCoverPictureUrl);
        }
    }

    public boolean changePassword(Long userId, ChangePasswordRequest changePasswordRequest) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();

            // Check if the current password matches
            if (passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
                // Update the password with the new one
                user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
                userRepository.save(user);
                return true;
            } else {
                // Current password doesn't match
                System.out.println("no match password");
                return false;
            }
        }
        return false;
    }

}
