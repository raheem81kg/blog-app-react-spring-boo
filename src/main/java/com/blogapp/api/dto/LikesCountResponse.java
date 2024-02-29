package com.blogapp.api.dto;

import lombok.Data;

@Data
public class LikesCountResponse {
    private long likesCount;

    public LikesCountResponse(long likesCount) {
        this.likesCount = likesCount;
    }
}
