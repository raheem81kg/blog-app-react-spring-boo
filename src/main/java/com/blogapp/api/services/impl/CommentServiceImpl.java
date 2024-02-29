package com.blogapp.api.services.impl;

import com.blogapp.api.dto.CommentDto;
import com.blogapp.api.dto.UserDto;
import com.blogapp.api.exceptions.CommentNotFoundException;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.models.Comment;
import com.blogapp.api.models.Post;
import com.blogapp.api.models.UserEntity;
import com.blogapp.api.repository.CommentRepository;
import com.blogapp.api.repository.PostRepository;
import com.blogapp.api.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentDto createComment(CommentDto commentDto) {
        // Check if postId is provided
        Long postId = commentDto.getPostId();
        if (postId == null) {
            throw new IllegalArgumentException("postId is required to create a comment.");
        }

        // Check if the associated Post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Ifn the Post exists, proceed to create the comment
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(commentDto.getUserId());
        comment.setContent(commentDto.getContent());

        Comment savedComment = commentRepository.save(comment);

        return convertToDto(savedComment);
    }



    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty()) {
            throw new CommentNotFoundException("No comments found for post with ID: " + postId);
        }

        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        commentRepository.delete(comment);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setUserId(comment.getUserId());
        commentDto.setPostId(comment.getPostId());
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setCommenter(mapToUserDto(comment.getUser()));
        return commentDto;
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
}
