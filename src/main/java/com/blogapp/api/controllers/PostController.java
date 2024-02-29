package com.blogapp.api.controllers;

import com.blogapp.api.dto.PostDto;
import com.blogapp.api.dto.PostResponse;
import com.blogapp.api.exceptions.PostNotFoundException;
import com.blogapp.api.services.PostService;
import com.blogapp.api.services.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    @Autowired
    public PostController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity<PostResponse> getAllPosts(
            @RequestParam (value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam (value = "pageSize", defaultValue = "10") int pageSize){
        return new ResponseEntity<>(postService.getAllPosts(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<PostDto> postDetail(@PathVariable long id){
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto){
        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @GetMapping("/getPostsByUser")
    public ResponseEntity<List<PostDto>> getPostsByUserId(@RequestParam (value = "targetUserId", required = true) long targetUserId
//                                                    ,@RequestParam (value = "pageNo", defaultValue = "0") int pageNo,
//                                                    @RequestParam (value = "pageSize", defaultValue = "10") int pageSize
    ){
        return new ResponseEntity<>(postService.getPostsByUserId(targetUserId), HttpStatus.OK);
    }

    @GetMapping("/getPostsByUserFollows")
    public ResponseEntity<PostResponse> getPostsByUserFollows(
            @RequestParam(value = "userId") long userId,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        return new ResponseEntity<>(postService.getPostsByUserFollows(userId, pageNo, pageSize), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePost(@PathVariable long id) {
        try {
            postService.deletePost(id);
            return new ResponseEntity<>("Post successfully deleted.", HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
