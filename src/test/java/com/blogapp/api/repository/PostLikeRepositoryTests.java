package com.blogapp.api.repository;

import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostLike;
import com.blogapp.api.models.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PostLikeRepositoryTests {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostLikeRepositoryTests(
            PostLikeRepository postLikeRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void PostLikeRepository_SaveAll_ReturnSavedPostLike() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        PostLike postLike = new PostLike(testUser, testPost);

        // Act
        PostLike savedPostLike = postLikeRepository.save(postLike);

        // Assert
        Assertions.assertThat(savedPostLike).isNotNull();
        Assertions.assertThat(savedPostLike.getUserId()).isEqualTo(testUser.getUserId());
        Assertions.assertThat(savedPostLike.getPostId()).isEqualTo(testPost.getPostId());
    }

    @Test
    public void PostLikeRepository_CountByPostId_ReturnPostLikeCount() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        PostLike postLike = new PostLike(testUser, testPost);
        postLikeRepository.save(postLike);

        // Act
        long postLikeCount = postLikeRepository.countByPostId(testPost.getPostId());

        // Assert
        Assertions.assertThat(postLikeCount).isEqualTo(1);
    }

    @Test
    public void PostLikeRepository_ExistsByUserIdAndPostId_ReturnTrue() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        PostLike postLike = new PostLike(testUser, testPost);
        postLikeRepository.save(postLike);

        // Act
        boolean exists = postLikeRepository.existsByUserIdAndPostId(testUser.getUserId(), testPost.getPostId());

        // Assert
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    public void PostLikeRepository_DeleteByUserAndPost() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        PostLike postLike = new PostLike(testUser, testPost);
        postLikeRepository.save(postLike);

        // Act
        postLikeRepository.deleteByUserAndPost(testUser, testPost);

        // Assert
        Assertions.assertThat(postLikeRepository.existsByUserIdAndPostId(testUser.getUserId(), testPost.getPostId())).isFalse();
    }
}
