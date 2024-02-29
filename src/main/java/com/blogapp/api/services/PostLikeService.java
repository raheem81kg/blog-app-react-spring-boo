package com.blogapp.api.services;

import com.blogapp.api.dto.LikesCountResponse;

public interface PostLikeService {

    LikesCountResponse getLikesCountByPostId(Long postId);

    boolean checkIfLiked(Long userId, Long postId);

    void likePost(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);
}
