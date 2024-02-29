package com.blogapp.api.controllers;

import com.blogapp.api.dto.LikeCheckDto;
import com.blogapp.api.dto.LikesCountResponse;
import com.blogapp.api.dto.PostLikeDto;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.services.PostLikeService;
import com.blogapp.api.services.impl.PostLikeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Autowired
    public PostLikeController(PostLikeServiceImpl postLikeService) {
        this.postLikeService = postLikeService;
    }

    @GetMapping("/getLikesCount")
    public ResponseEntity<LikesCountResponse> getLikesCountByPostId(@RequestParam Long postId) {
        LikesCountResponse likesCountResponse = postLikeService.getLikesCountByPostId(postId);
        return new ResponseEntity<>(likesCountResponse, HttpStatus.OK);
    }

    @PostMapping("/checkIfLiked")
    public ResponseEntity<LikeCheckDto> checkIfLiked(@RequestBody PostLikeDto post) {
        return new ResponseEntity<>(new LikeCheckDto(postLikeService.checkIfLiked(post.getUserId(), post.getPostId())), HttpStatus.OK);
    }

    @PostMapping("/likePost")
    public ResponseEntity<String> likePost(@RequestBody PostLikeDto post) {
        try {
            postLikeService.likePost(post.getUserId(), post.getPostId());
            return new ResponseEntity<>("Post successfully liked.", HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/unlikePost")
    public ResponseEntity<String> unlikePost(@RequestBody PostLikeDto post) {
        try {
            postLikeService.unlikePost(post.getUserId(), post.getPostId());
            return new ResponseEntity<>("Post successfully unliked.", HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
