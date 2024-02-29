package com.blogapp.api.dto;

import lombok.Data;

@Data
public class PostLikeDto {
    private Long userId;
    private Long postId;
}
