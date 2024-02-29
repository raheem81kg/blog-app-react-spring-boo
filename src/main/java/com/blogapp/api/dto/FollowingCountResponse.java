package com.blogapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowingCountResponse {
    private long followingCount;
}
