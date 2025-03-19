package com.vgt.collections.ErrorHandler;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException{
    public NotFoundException(String message, String methodName) {

        super(message, HttpStatus.NOT_FOUND, methodName);
    }
}
