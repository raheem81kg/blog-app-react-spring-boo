package com.blogapp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowerCountResponse {
    private long followerCount;
}
