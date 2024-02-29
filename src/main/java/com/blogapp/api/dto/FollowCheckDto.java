package com.blogapp.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FollowCheckDto {
    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public FollowCheckDto(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}