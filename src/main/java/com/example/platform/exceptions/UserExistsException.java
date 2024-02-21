package com.example.platform.exceptions;

public class UserExistsException extends Throwable {
    public UserExistsException(){
        super("User under this email already exists.");
    }
}
