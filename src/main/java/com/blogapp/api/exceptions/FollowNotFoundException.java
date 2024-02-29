package com.blogapp.api.exceptions;

public class FollowNotFoundException extends RuntimeException {

    public FollowNotFoundException(String message) {
        super(message);
    }

    public FollowNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
