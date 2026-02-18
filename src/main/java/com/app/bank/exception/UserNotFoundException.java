package com.app.bank.exception;

public class UserNotFoundException  extends Exception{
    public UserNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
