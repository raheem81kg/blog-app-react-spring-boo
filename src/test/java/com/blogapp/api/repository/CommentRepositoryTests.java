package com.blogapp.api.repository;

import com.blogapp.api.models.Comment;
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
public class CommentRepositoryTests {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentRepositoryTests(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void CommentRepository_SaveAll_ReturnSavedComment() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        Comment comment = Comment.builder().userId(testUser.getUserId()).postId(testPost.getPostId()).content("Sample comment content").build();

        // Act
        Comment savedComment = commentRepository.save(comment);

        // Assert
        Assertions.assertThat(savedComment).isNotNull();
        Assertions.assertThat(savedComment.getCommentId()).isGreaterThan(0);
    }

    @Test
    public void CommentRepository_FindByPostId_ReturnCommentsForPost() {
        // Arrange

        UserEntity testUser = userRepository.save(UserEntity.builder()
                .name("John Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .passwordHash("hashedPassword")
                .build());

        Post testPost = postRepository.save(Post.builder().userId(testUser.getUserId()).content("Sample post content").build());

        Comment comment1 = Comment.builder().userId(testUser.getUserId()).postId(testPost.getPostId()).content("Sample comment content 1").build();
        Comment comment2 = Comment.builder().userId(testUser.getUserId()).postId(testPost.getPostId()).content("Sample comment content 2").build();

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        // Act
        List<Comment> postComments = commentRepository.findByPostId(testPost.getPostId());

        // Assert
        Assertions.assertThat(postComments).isNotNull();
        Assertions.assertThat(postComments.size()).isEqualTo(2);
    }
}
