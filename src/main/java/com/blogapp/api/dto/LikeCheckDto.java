package com.blogapp.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LikeCheckDto {
    @JsonProperty("isLiked")
    private boolean isLiked;

}
