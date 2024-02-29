package com.blogapp.api.repository;

import com.blogapp.api.models.Post;
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
public class PostRepositoryTests {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostRepositoryTests(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void PostRepository_SaveAll_ReturnSavedPost() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post post = Post.builder().userId(testUser.getUserId()).content("Sample content").build();

        // Act
        Post savedPost = postRepository.save(post);

        // Assert
        Assertions.assertThat(savedPost).isNotNull();
        Assertions.assertThat(savedPost.getPostId()).isGreaterThan(0);
    }

    @Test
    public void PostRepository_FindByUserId_ReturnPostsForUser() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post post1 = Post.builder().userId(testUser.getUserId()).content("Sample content").build();
        Post post2 = Post.builder().userId(testUser.getUserId()).content("Sample content").build();

        postRepository.save(post1);
        postRepository.save(post2);

        // Act
        List<Post> userPosts = postRepository.findByUserId(testUser.getUserId());

        // Assert
        Assertions.assertThat(userPosts).isNotNull();
        Assertions.assertThat(userPosts.size()).isEqualTo(2);
    }

}
