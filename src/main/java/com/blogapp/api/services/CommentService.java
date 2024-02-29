package com.blogapp.api.services;

import com.blogapp.api.dto.CommentDto;
import com.blogapp.api.dto.PostDto;
import com.blogapp.api.dto.PostResponse;
import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto);
    List<CommentDto> getCommentsByPostId(long id);
    void deleteComment(long id);
}
