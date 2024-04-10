package com.example.platform.exceptions;

public class UserNotFoundException extends Throwable{
    public UserNotFoundException(){
        super("User under current email cannot be found.");
    }

    public UserNotFoundException(String message){super(message);}
}
