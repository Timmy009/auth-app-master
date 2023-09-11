package com.example.ecommerce.exception;

public class UserAlreadyExistException extends CaseStudyBaseException{
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
