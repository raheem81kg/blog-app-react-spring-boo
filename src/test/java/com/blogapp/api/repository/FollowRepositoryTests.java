package com.blogapp.api.repository;

import com.blogapp.api.models.Follow;
import com.blogapp.api.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class FollowRepositoryTests {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowRepositoryTests(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void FollowRepository_SaveFollow_ReturnSavedFollow() {
        // Arrange
        UserEntity follower = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        UserEntity following = userRepository.save(UserEntity.builder()
                .name("Jane Doe")
                .username("jane.doe")
                .email("jane.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Follow follow = new Follow();
        follow.setFollowerId(follower.getUserId());
        follow.setFollowingId(following.getUserId());

        // Act
        Follow savedFollow = followRepository.save(follow);

        // Assert
        Assertions.assertThat(savedFollow).isNotNull();
        Assertions.assertThat(savedFollow.getFollowerId()).isEqualTo(follower.getUserId());
        Assertions.assertThat(savedFollow.getFollowingId()).isEqualTo(following.getUserId());
    }

    @Test
    public void FollowRepository_CountByFollowingId_ReturnFollowCount() {
        // Arrange
        UserEntity follower = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        UserEntity following = userRepository.save(UserEntity.builder()
                .name("Jane Doe")
                .username("jane.doe")
                .email("jane.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Follow follow = new Follow();
        follow.setFollowerId(follower.getUserId());
        follow.setFollowingId(following.getUserId());

        followRepository.save(follow);

        // Act
        long followCount = followRepository.countByFollowingId(following.getUserId());

        // Assert
        Assertions.assertThat(followCount).isEqualTo(1);
    }

    @Test
    public void FollowRepository_GetFollowedUserIdsByFollowerId_ReturnUserIds() {
        // Arrange
        UserEntity follower = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        UserEntity following = userRepository.save(UserEntity.builder()
                .name("Jane Doe")
                .username("jane.doe")
                .email("jane.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Follow follow = new Follow();
        follow.setFollowerId(follower.getUserId());
        follow.setFollowingId(following.getUserId());

        followRepository.save(follow);

        // Act
        List<Long> followedUserIds = followRepository.getFollowedUserIdsByFollowerId(follower.getUserId());

        // Assert
        Assertions.assertThat(followedUserIds).isNotNull();
        Assertions.assertThat(followedUserIds.size()).isEqualTo(1);
        Assertions.assertThat(followedUserIds.get(0)).isEqualTo(following.getUserId());
    }

    @Test
    public void FollowRepository_CountByFollowerId_ReturnFollowCount() {
        // Arrange
        UserEntity follower = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        UserEntity following = userRepository.save(UserEntity.builder()
                .name("Jane Doe")
                .username("jane.doe")
                .email("jane.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Follow follow = new Follow();
        follow.setFollowerId(follower.getUserId());
        follow.setFollowingId(following.getUserId());

        followRepository.save(follow);

        // Act
        long followCount = followRepository.countByFollowerId(follower.getUserId());

        // Assert
        Assertions.assertThat(followCount).isEqualTo(1);
    }
}
