package com.blogapp.api.services;

import com.blogapp.api.dto.PostDto;
import com.blogapp.api.dto.PostResponse;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostImage;
import com.blogapp.api.repository.FollowRepository;
import com.blogapp.api.repository.PostImageRepository;
import com.blogapp.api.repository.PostRepository;
import com.blogapp.api.services.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_withImages_returnsPostDto() {
        // Arrange
        PostDto postDto = new PostDto();
        postDto.setUserId(1L);
        postDto.setContent("Test content");
        postDto.setImages(List.of("image1.jpg", "image2.jpg"));

        Post savedPost = new Post();
        savedPost.setPostId(1L);
        savedPost.setUserId(postDto.getUserId());
        savedPost.setContent(postDto.getContent());
        savedPost.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postRepository.findById(savedPost.getPostId())).thenReturn(Optional.of(savedPost));

        // Mock the behavior of postImageRepository.findByPostId
        List<PostImage> savedImages = postDto.getImages().stream()
                .map(imageUrl -> {
                    PostImage postImage = new PostImage();
                    postImage.setPost(savedPost);
                    postImage.setImageUrl(imageUrl);
                    return postImage;
                })
                .collect(Collectors.toList());

        when(postImageRepository.findByPostId(savedPost.getPostId())).thenReturn(savedImages);

        // Act
        PostDto result = postService.createPost(postDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPostId()).isEqualTo(savedPost.getPostId());
        assertThat(result.getUserId()).isEqualTo(savedPost.getUserId());
        assertThat(result.getContent()).isEqualTo(savedPost.getContent());
        assertThat(result.getCreatedAt()).isEqualTo(savedPost.getCreatedAt());
        assertThat(result.getImages()).isEqualTo(postDto.getImages());

        // Verify
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postRepository, times(1)).findById(savedPost.getPostId());
        verify(postImageRepository, times(2)).save(any(PostImage.class)); // Ensure images are saved individually
        verify(postImageRepository, times(1)).findByPostId(savedPost.getPostId());
    }

    @Test
    void getAllPosts_returnsPostResponse() {
        // Arrange
        int pageNo = 0;
        int pageSize = 10;

        // Initialize a list of 10 posts
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Post post = new Post();
            post.setPostId((long) i);
            post.setUserId((long) i);
            post.setContent("Content " + i);
            post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            posts.add(post);
        }

        // Mock the Page object
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(pageNo, pageSize), posts.size());

        // Mock the PostRepository behavior
        when(postRepository.findAll(any(Pageable.class))).thenReturn(postPage);

        // Act
        PostResponse result = postService.getAllPosts(pageNo, pageSize);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPosts()).hasSize(posts.size());
        assertThat(result.getPageNo()).isEqualTo(pageNo);
        assertThat(result.getPageSize()).isEqualTo(pageSize);
        assertThat(result.getTotalElements()).isEqualTo(posts.size());
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.isLast()).isEqualTo(true);

        // Verify that the findAll method was invoked with the correct Pageable parameters
        verify(postRepository, times(1)).findAll(any(Pageable.class));

        // Ensure no other interactions occurred with the postRepository
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void getPostById_existingId_returnsPostDto() {
        // Arrange
        long postId = 1L;

        Post post = new Post();
        post.setPostId(postId);
        post.setUserId(1L);
        post.setContent("Test content");
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        PostDto result = postService.getPostById(postId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPostId()).isEqualTo(post.getPostId());
        assertThat(result.getUserId()).isEqualTo(post.getUserId());
        assertThat(result.getContent()).isEqualTo(post.getContent());
        assertThat(result.getCreatedAt()).isEqualTo(post.getCreatedAt());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void getPostById_nonExistingId_throwsPostNotFoundException() {
        // Arrange
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> postService.getPostById(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found with id: " + postId);

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void updatePost_existingId_returnsUpdatedPostDto() {
        // Arrange
        long postId = 1L;

        Post existingPost = new Post();
        existingPost.setPostId(postId);
        existingPost.setUserId(1L);
        existingPost.setContent("Old content");
        existingPost.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        PostDto updatedPostDto = new PostDto();
        updatedPostDto.setContent("New content");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        // Act
        PostDto result = postService.updatePost(postId, updatedPostDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPostId()).isEqualTo(existingPost.getPostId());
        assertThat(result.getUserId()).isEqualTo(existingPost.getUserId());
        assertThat(result.getContent()).isEqualTo(updatedPostDto.getContent());
        assertThat(result.getCreatedAt()).isEqualTo(existingPost.getCreatedAt());

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void updatePost_nonExistingId_throwsPostNotFoundException() {
        // Arrange
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> postService.updatePost(postId, new PostDto()))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found with id: " + postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getPostsByUserId_existingUserId_returnsListOfPostDtos() {
        // Arrange
        long userId = 1L;

        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Post post = new Post();
            post.setPostId((long) i);
            post.setUserId(userId);
            post.setContent("Content " + i);
            post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            posts.add(post);
        }

        when(postRepository.findByUserId(userId)).thenReturn(posts);

        // Act
        List<PostDto> result = postService.getPostsByUserId(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(posts.size());

        verify(postRepository, times(1)).findByUserId(userId);
    }

    @Test
    void getPostsByUserId_nonExistingUserId_throwsPostNotFoundException() {
        // Arrange
        long userId = 1L;

        when(postRepository.findByUserId(userId)).thenReturn(List.of());

        // Act and Assert
        assertThatThrownBy(() -> postService.getPostsByUserId(userId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("No posts found for user with ID: " + userId);

        verify(postRepository, times(1)).findByUserId(userId);
    }

//    @Test
//    void getPostsByUserFollows_existingUserId_returnsListOfPostDtos() {
//        // Arrange
//        long userId = 1L;
//
//        List<Long> followedUserIds = List.of(2L, 3L, 4L);
//
//        List<Post> posts = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            Post post = new Post();
//            post.setPostId((long) i);
//            post.setUserId((long) i);
//            post.setContent("Content " + i);
//            post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//            posts.add(post);
//        }
//
//        // Ensure that getFollowedUserIdsByFollowerId returns the followedUserIds
//        when(followRepository.getFollowedUserIdsByFollowerId(userId)).thenReturn(List.of(2L, 3L, 4L));
//
//        // Mock the behavior of postRepository.getPostsByUserIds to return posts
//        when(postRepository.getPostsByUserIds(followedUserIds)).thenReturn(posts);
//
//        // Act
//        List<PostDto> result = postService.getPostsByUserFollows(userId, 0, 5);
//
//        System.out.println("Followed User IDs: " + followedUserIds);
//        System.out.println("Posts: " + posts);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(posts.size());
//
//        // Verify the number of invocations
//        verify(followRepository, times(1)).getFollowedUserIdsByFollowerId(userId);
//        verify(postRepository, times(1)).getPostsByUserIds(followedUserIds);
//        verifyNoMoreInteractions(followRepository, postRepository);
//
//    }

//    @Test
//    void getPostsByUserFollows_nonExistingUserId_throwsPostNotFoundException() {
//        // Arrange
//        long userId = 1L;
//
//        // Ensure that getFollowedUserIdsByFollowerId returns an empty list
//        when(followRepository.getFollowedUserIdsByFollowerId(userId)).thenReturn(List.of());
//
//        // Mock the behavior of postRepository.getPostsByUserIds to return an empty list
//        when(postRepository.getPostsByUserIds(Collections.emptyList())).thenReturn(Collections.emptyList());
//
//        // Act and Assert
//        assertThatThrownBy(() -> postService.getPostsByUserFollows(userId, 0, 5))
//                .isInstanceOf(PostNotFoundException.class)
//                .hasMessage("Error fetching posts by user follows: No posts found for user with ID: " + userId);
//
//        // Verify the number of invocations
//        verify(followRepository, times(1)).getFollowedUserIdsByFollowerId(userId);
//        verify(postRepository, times(1)).getPostsByUserIds(Collections.emptyList());
//        verifyNoMoreInteractions(followRepository, postRepository);
//    }



    @Test
    void deletePost_existingId_deletesPost() {
        // Arrange
        long postId = 1L;

        Post post = new Post();
        post.setPostId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(postId);

        // Assert
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePost_nonExistingId_throwsPostNotFoundException() {
        // Arrange
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThatThrownBy(() -> postService.deletePost(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("Post not found with id: " + postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, never()).delete(any(Post.class));
    }
}
