package com.blogapp.api.services;

import com.blogapp.api.dto.FollowDto;

public interface FollowService {
    boolean checkIfFollowed(Long followerId, Long followingId);

    void followUser(FollowDto followDto);

    void unfollowUser(FollowDto followDto);

    long getFollowerCount(Long userId);

    long getFollowingCount(Long userId);
}
