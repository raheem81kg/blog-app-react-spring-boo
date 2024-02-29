package com.blogapp.api.services.impl;

import com.blogapp.api.dto.PostDto;
import com.blogapp.api.dto.PostResponse;
import com.blogapp.api.dto.UserDto;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostImage;
import com.blogapp.api.models.UserEntity;
import com.blogapp.api.repository.*;
import com.blogapp.api.services.CommentService;
import com.blogapp.api.services.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, PostImageRepository postImageRepository, FollowRepository followRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public PostDto createPost(PostDto postDto) {
        Post post = mapToPostEntity(postDto);
        Post newPost = postRepository.save(post);

        // Check if the post has associated images
        if (postDto.getImages() != null && !postDto.getImages().isEmpty()) {
            addImageToPost(newPost, postDto.getImages());
        }

        // Fetch the saved post again to include images in the response
        newPost = postRepository.findById(newPost.getPostId()).orElse(null);

        assert newPost != null;
        return mapToPostDto(newPost);
    }
    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Post> posts = postRepository.findAll(pageable);
        List<Post> listOfPosts = posts.getContent();
        List<PostDto> content = listOfPosts.stream().map(this::mapToPostDto).toList();

        return new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return mapToPostDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(long id, PostDto updatedPostDto) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            existingPost.setContent(updatedPostDto.getContent());

            // Save the updated post
            Post updatedPost = postRepository.save(existingPost);
            return mapToPostDto(updatedPost);
        } else {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
    }

    @Override
    public List<PostDto> getPostsByUserId(long userId) {

        try {
            List<Post> posts = postRepository.findByUserId(userId);

            if (posts.isEmpty()) {
                throw new PostNotFoundException("No posts found for user with ID: " + userId);
            }

            return posts.stream().map(this::mapToPostDto).collect(Collectors.toList());
        } catch (Exception e) {
            throw new PostNotFoundException("No posts found for user with ID: " + userId);
        }
    }

    @Override
    public PostResponse getPostsByUserFollows(long userId, int pageNo, int pageSize) {

//        Pageable pageable = PageRequest.of(pageNo, pageSize);
//        Page<Post> posts = postRepository.findAll(pageable);
//        List<Post> listOfPosts = posts.getContent();
//        List<PostDto> content = listOfPosts.stream().map(this::mapToPostDto).toList();
//
//        return new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());

        try {

            // Fetch followed users
            List<Long> followedUserIds = followRepository.getFollowedUserIdsByFollowerId(userId);

            // Add the user's own ID to include their posts in the feed
            followedUserIds.add(userId);

            Pageable pageable = PageRequest.of(pageNo, pageSize);


            // Fetch posts of followed users
            Page<Post> posts = postRepository.getPostsByUserIds(followedUserIds, pageable);

            List<Post> listOfPosts = posts.getContent();
            List<PostDto> content = listOfPosts.stream().map(this::mapToPostDto).toList();

            return new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
        } catch (Exception e) {
            throw new PostNotFoundException("Error fetching posts by user follows: No posts found for user with ID: " + userId);
        }
    }

    @Override
    @Transactional
    public void deletePost(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        postRepository.delete(post);
    }

    private Post mapToPostEntity(PostDto postDto) {
        Post post = new Post();
        post.setUserId(postDto.getUserId());
        post.setContent(postDto.getContent());
        return post;
    }

    private PostDto mapToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setPostId(post.getPostId());
        postDto.setUserId(post.getUserId());
        postDto.setContent(post.getContent());
        postDto.setCreatedAt(post.getCreatedAt());
        postDto.setUpdatedAt(post.getUpdatedAt());

        // Fetch images associated with the post
        // could've done fetch type eager
        List<String> images = postImageRepository.findByPostId(post.getPostId())
                .stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());

        postDto.setImages(images);

        // Fetch the user associated with the post
        Optional<UserEntity> userOptional = userRepository.findById(post.getUserId());
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            UserDto userDto = mapToUserDto(user);
            postDto.setUser(userDto);
        }

        // Fetch the number of comments associated with the post
        Long commentsNum = commentRepository.countByPostId(post.getPostId());
        postDto.setCommentsNum(commentsNum.intValue());

        return postDto;
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


    @Transactional
    private void addImageToPost(PostDto postDto) {
        try {
            // Check if the post has associated images
            if (postDto.getImages() != null && !postDto.getImages().isEmpty()) {
                Optional<Post> optionalPost = postRepository.findById(postDto.getPostId());
                if (optionalPost.isPresent()) {
                    addImageToPost(optionalPost.get(), postDto.getImages());
                } else {
                    // Handle case when post is not found
                    throw new RuntimeException("Post not found with ID: " + postDto.getPostId());
                }
            }
        } catch (Exception e) {
            // Handle exceptions
            throw new RuntimeException("Error adding images to post: " + e.getMessage(), e);
        }
    }

    @Transactional
    private void addImageToPost(Post post, List<String> images) {
        try {
            // Iterate over the images and add them to the database
            for (String imageUrl : images) {
                PostImage postImage = new PostImage();
                postImage.setPost(post);
                postImage.setImageUrl(imageUrl);
                postImage.setPostId(post.getPostId());

                postImageRepository.save(postImage);
            }
        } catch (Exception e) {
            // Handle exceptions
            throw new RuntimeException("Error adding images to post: " + e.getMessage(), e);
        }
    }
}

