package com.blogapp.api.dto;

import com.blogapp.api.models.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
public class PostDto {
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("user_id")
    private Long userId;
    private UserDto user;
    private String content;
    private Integer commentsNum;
    private List<String> images;
    @JsonProperty("created_at")
    private Timestamp createdAt;
    @JsonProperty("updated_at")
    private Timestamp updatedAt;
}
