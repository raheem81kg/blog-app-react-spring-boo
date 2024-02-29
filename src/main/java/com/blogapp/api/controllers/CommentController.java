package com.blogapp.api.controllers;

import com.blogapp.api.dto.CommentDto;
import com.blogapp.api.services.CommentService;
import com.blogapp.api.exceptions.CommentNotFoundException;
import com.blogapp.api.services.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(commentDto);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteComment(@RequestBody CommentDto commentDto) {
        Long commentId = commentDto.getCommentId();
        if (commentId == null) {
            return new ResponseEntity<>("Comment ID is required in the request body.", HttpStatus.BAD_REQUEST);
        }
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("Comment successfully deleted.", HttpStatus.OK);
        } catch (CommentNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
