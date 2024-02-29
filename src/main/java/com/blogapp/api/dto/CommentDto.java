package com.blogapp.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class CommentDto {
    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("post_id")
    private Long postId;
    private String content;
    @JsonProperty("created_at")
    private Timestamp createdAt;
    private UserDto commenter;
}
