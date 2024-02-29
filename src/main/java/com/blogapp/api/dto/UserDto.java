package com.blogapp.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class UserDto {
    @JsonProperty("user_id")
    private Long userId;
    private String name;
    private String username;
    private String email;
    @JsonProperty("profilePic")
    private String profilePictureUrl;
    @JsonProperty("coverPic")
    private String coverPictureUrl;
    private String bio;
}
