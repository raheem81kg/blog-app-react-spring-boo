package com.blogapp.api.controllers;

import com.blogapp.api.dto.LoginDto;
import com.blogapp.api.dto.LoginResponse;
import com.blogapp.api.dto.RegisterDto;
import com.blogapp.api.dto.UserDto;
import com.blogapp.api.models.Role;
import com.blogapp.api.models.UserEntity;
import com.blogapp.api.repository.RoleRepository;
import com.blogapp.api.repository.UserRepository;
import com.blogapp.api.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if (userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>("Username is taken", HttpStatus.BAD_REQUEST);
        } else if (userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email is taken", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        mapToUser(registerDto, user);

        Optional<Role> role = roleRepository.findByName("USER");
        user.setRoles(Collections.singletonList(role.orElseThrow(() ->
                new RuntimeException("Role 'USER' not found"))));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    private void mapToUser(RegisterDto registerDto, UserEntity user) {
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String usernameOrEmail = loginDto.getUsernameOrEmail();

        // Determine if the provided input is an email or a username
        Optional<UserEntity> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);

        if (userOptional.isEmpty()) {
            // Handle invalid email or username (user not found)
            return new ResponseEntity<>("Invalid email or username", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userOptional.get();

        // Continue with authentication logic
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);

        UserDto userDto = new UserDto();
        mapToDto(userDto, user);

        return ResponseEntity.ok(new LoginResponse(userDto, token));
    }

    private void mapToDto(UserDto userDto, UserEntity user) {
        userDto.setUserId(user.getUserId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setBio(user.getBio());
        userDto.setCoverPictureUrl(user.getCoverPictureUrl());
        userDto.setProfilePictureUrl(user.getProfilePictureUrl());
    }

}
