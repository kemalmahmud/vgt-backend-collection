package com.vgt.collections.ErrorHandler;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseException extends RuntimeException {

    private String message = "Internal Server Error";
    private Object data = null;
    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private String methodName;

    public BaseException(String message, HttpStatus httpStatus, String methodName) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.methodName = methodName;
    }

    public BaseException(String message, HttpStatus httpStatus, String methodName, Object data) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
        this.methodName = methodName;
        this.data = data;
    }

}
