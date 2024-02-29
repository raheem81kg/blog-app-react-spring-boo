package com.blogapp.api.repository;

import com.blogapp.api.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTests {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void UserRepository_SaveUser_ReturnSavedUser() {
        // Arrange
        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build();

        // Act
        UserEntity savedUser = userRepository.save(user);

        // Assert
        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getUserId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_FindByUsername_ReturnUser() {
        // Arrange
        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build();

        userRepository.save(user);

        // Act
        Optional<UserEntity> foundUser = userRepository.findByUsername("john.doe");

        // Assert
        Assertions.assertThat(foundUser).isPresent();
        Assertions.assertThat(foundUser.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    public void UserRepository_ExistsByUsername_ReturnTrue() {
        // Arrange
        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build();

        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByUsername("john.doe");

        // Assert
        Assertions.assertThat(exists).isTrue();
    }
}
