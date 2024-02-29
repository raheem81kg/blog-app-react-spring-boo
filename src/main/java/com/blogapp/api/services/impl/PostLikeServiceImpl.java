package com.blogapp.api.services.impl;

import com.blogapp.api.dto.LikesCountResponse;
import com.blogapp.api.dto.PostLikeDto;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.models.Post;
import com.blogapp.api.models.PostLike;
import com.blogapp.api.models.UserEntity;
import com.blogapp.api.repository.PostLikeRepository;
import com.blogapp.api.repository.PostRepository;
import com.blogapp.api.repository.UserRepository;
import com.blogapp.api.services.PostLikeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostLikeServiceImpl(PostLikeRepository postLikeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LikesCountResponse getLikesCountByPostId(Long postId) {
        long likesCount = postLikeRepository.countByPostId(postId);
        return new LikesCountResponse(likesCount);
    }

    @Override
    public boolean checkIfLiked(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    public void likePost(Long userId, Long postId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<Post> postOptional = postRepository.findById(postId);

        if (userOptional.isEmpty() || postOptional.isEmpty()) {
            throw new PostNotFoundException("User or Post not found.");
        }

        UserEntity user = userOptional.get();
        Post post = postOptional.get();

        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

    }

    @Override
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        Optional<Post> postOptional = postRepository.findById(postId);

        if (userOptional.isEmpty() || postOptional.isEmpty()) {
            throw new PostNotFoundException("User or Post not found.");
        }

        UserEntity user = userOptional.get();
        Post post = postOptional.get();

        postLikeRepository.deleteByUserAndPost(user, post);
    }

    private PostLikeDto convertToDto(PostLike postLike) {
        // Convert PostLike entity to PostLikeDto
        PostLikeDto postLikeDto = new PostLikeDto();
        postLikeDto.setUserId(postLike.getUserId());
        postLikeDto.setPostId(postLike.getPostId());
        // Set other properties if needed
        return postLikeDto;
    }
}
