package com.blogapp.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorObject> handlePostNotFoundException(PostNotFoundException ex, WebRequest request){
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorObject> handleCommentNotFoundException(CommentNotFoundException ex, WebRequest request){
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ErrorObject> handleFollowNotFoundException(FollowNotFoundException ex, WebRequest request){
        ErrorObject errorObject = new ErrorObject(HttpStatus.NOT_FOUND.value(), ex.getMessage(), new Date(), request.getDescription(false));
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }
}
