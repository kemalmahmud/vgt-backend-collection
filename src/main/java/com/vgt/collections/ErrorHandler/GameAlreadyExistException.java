package com.vgt.collections.ErrorHandler;

import org.springframework.http.HttpStatus;

public class GameAlreadyExistException extends BaseException{
    public GameAlreadyExistException(String methodName) {

        super("Game already exist", HttpStatus.NOT_FOUND, methodName);
    }
}
