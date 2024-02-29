package com.blogapp.api.repository;

import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostImage;
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
public class PostImageRepositoryTests {

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostImageRepositoryTests(
            PostImageRepository postImageRepository,
            PostRepository postRepository, UserRepository userRepository) {
        this.postImageRepository = postImageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void PostImageRepository_SaveAll_ReturnSavedPostImage() {
        // Arrange

        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build();
        UserEntity savedUser = userRepository.save(user);

        Post testPost = postRepository.save(Post.builder().content("Sample post content").userId(savedUser.getUserId()).build());

        PostImage postImage = PostImage.builder().postId(testPost.getPostId()).imageUrl("sample-image-url.jpg").build();

        // Act
        PostImage savedPostImage = postImageRepository.save(postImage);

        // Assert
        Assertions.assertThat(savedPostImage).isNotNull();
        Assertions.assertThat(savedPostImage.getImageId()).isGreaterThan(0);
    }

    @Test
    public void PostImageRepository_FindByPostId_ReturnPostImagesForPost() {
        // Arrange

        UserEntity user = UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build();
        UserEntity savedUser = userRepository.save(user);

        Post testPost = postRepository.save(Post.builder().content("Sample post content").userId(savedUser.getUserId()).build());

        PostImage postImage1 = PostImage.builder().postId(testPost.getPostId()).imageUrl("image1.jpg").build();
        PostImage postImage2 = PostImage.builder().postId(testPost.getPostId()).imageUrl("image2.jpg").build();

        postImageRepository.save(postImage1);
        postImageRepository.save(postImage2);

        // Act
        List<PostImage> postImages = postImageRepository.findByPostId(testPost.getPostId());

        // Assert
        Assertions.assertThat(postImages).isNotNull();
        Assertions.assertThat(postImages.size()).isEqualTo(2);
    }
}
