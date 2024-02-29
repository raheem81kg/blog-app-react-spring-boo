package com.blogapp.api.controllers;

import com.blogapp.api.dto.*;
import com.blogapp.api.services.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/doesFollow")
    public ResponseEntity<FollowCheckDto> doesFollow(@RequestBody FollowDto followDto) {
        boolean isFollowing = followService.checkIfFollowed(followDto.getFollowerId(), followDto.getFollowingId());
        return new ResponseEntity<>(new FollowCheckDto(isFollowing), HttpStatus.OK);
    }

    @PostMapping("/followUser")
    public ResponseEntity<MessageResponse> followUser(@RequestBody FollowDto followDto) {
        // ensure users exist
        followService.followUser(followDto);
        return new ResponseEntity<>(new MessageResponse("Successfully followed the user."), HttpStatus.OK);
    }

    @PostMapping("/unfollowUser")
    public ResponseEntity<MessageResponse> unfollowUser(@RequestBody FollowDto followDto) {
        followService.unfollowUser(followDto);
        return new ResponseEntity<>(new MessageResponse("Successfully unfollowed the user."), HttpStatus.OK);
    }

    @GetMapping("/getFollowerCount/{userId}")
    public ResponseEntity<FollowerCountResponse> getFollowerCount(@PathVariable Long userId) {
        long followerCount = followService.getFollowerCount(userId);
        return new ResponseEntity<>(new FollowerCountResponse(followerCount), HttpStatus.OK);
    }

    @GetMapping("/getFollowingCount/{userId}")
    public ResponseEntity<FollowingCountResponse> getFollowingCount(@PathVariable Long userId) {
        long followingCount = followService.getFollowingCount(userId);
        return new ResponseEntity<>(new FollowingCountResponse(followingCount), HttpStatus.OK);
    }
}
