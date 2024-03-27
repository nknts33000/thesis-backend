package com.example.platform.exceptions;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException(String message){
        //this.message=message;
        super(message);
    }
}
