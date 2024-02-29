package com.blogapp.api.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorObject {
    private Integer statusCode;
    private String message;
    private Date timestamp;
    private String path;

    public ErrorObject(Integer statusCode, String message, Date timestamp, String path) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
}
