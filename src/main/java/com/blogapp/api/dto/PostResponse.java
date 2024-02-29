package com.blogapp.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private List<PostDto> posts;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PostResponse(List<PostDto> posts, int pageNo, int pageSize, long totalElements, int totalPages, boolean last) {
        this.posts = posts;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
