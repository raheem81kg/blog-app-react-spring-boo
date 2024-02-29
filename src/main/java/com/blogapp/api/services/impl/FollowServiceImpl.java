package com.blogapp.api.services.impl;

import com.blogapp.api.dto.FollowDto;
import com.blogapp.api.exceptions.FollowNotFoundException;
import com.blogapp.api.models.Follow;
import com.blogapp.api.models.compositeKey.FollowId;
import com.blogapp.api.repository.FollowRepository;
import com.blogapp.api.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;

    @Autowired
    public FollowServiceImpl(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    @Override
    public boolean checkIfFollowed(Long followerId, Long followingId) {
        Optional<Follow> follow = followRepository.findById(new FollowId(followerId, followingId));
        return follow.isPresent();
    }

    @Override
    @Transactional
    public void followUser(FollowDto followDto) {
        Follow follow = mapToFollowEntity(followDto);
        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(FollowDto followDto) {
        FollowId followId = new FollowId(followDto.getFollowerId(), followDto.getFollowingId());
        Optional<Follow> followOptional = followRepository.findById(followId);

        if (followOptional.isPresent()) {
            followRepository.deleteById(followId);
        } else {
            throw new FollowNotFoundException("Follow relationship not found.");
        }
    }

    @Override
    public long getFollowerCount(Long userId) {
        return followRepository.countByFollowingId(userId);
    }

    @Override
    public long getFollowingCount(Long userId) {
        return followRepository.countByFollowerId(userId);
    }

    private Follow mapToFollowEntity(FollowDto followDto) {
        Follow follow = new Follow();
        follow.setFollowerId(followDto.getFollowerId());
        follow.setFollowingId(followDto.getFollowingId());
        return follow;
    }
}
