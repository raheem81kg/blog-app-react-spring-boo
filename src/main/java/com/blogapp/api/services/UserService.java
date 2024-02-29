package com.blogapp.api.services;

import com.blogapp.api.dto.ChangePasswordRequest;
import com.blogapp.api.dto.UserDto;

import java.util.List;

public interface UserService {
    public List<UserDto> getAllUsers();
    public List<UserDto> searchUsersByUsername(String username);

    public UserDto getUserProfile(Long userId);

    public UserDto updateUser(Long userId, UserDto updatedUserDto);
    public boolean changePassword(Long userId, ChangePasswordRequest changePasswordRequest);

    public void deleteUser(Long userId);
}
