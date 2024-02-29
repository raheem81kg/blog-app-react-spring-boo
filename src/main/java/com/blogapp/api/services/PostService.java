package com.blogapp.api.services;

import com.blogapp.api.dto.PostDto;
import com.blogapp.api.dto.PostResponse;
import java.util.List;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponse getAllPosts(int pageNo, int pageSize);
    PostDto getPostById(long id);
    PostDto updatePost(long id, PostDto updatedPostDto);
    List<PostDto> getPostsByUserId(long userId);
    PostResponse getPostsByUserFollows(long userId, int offset, int limit);
    void deletePost(long id);
}
