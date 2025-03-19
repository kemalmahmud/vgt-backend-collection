package com.vgt.collections.ErrorHandler;

import com.vgt.collections.Model.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CollectionServiceErrorHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exception(Exception e) {
        BaseResponse errorDetail = BaseResponse.builder().message("Something wrong happened").status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        return new ResponseEntity(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFoundException(NotFoundException e) {
        log.error("error in " + e.getMethodName());
        BaseResponse errorDetail = BaseResponse.builder().message(e.getMessage()).status(HttpStatus.NOT_FOUND.value()).build();
        return new ResponseEntity(errorDetail, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({GameAlreadyExistException.class})
    public ResponseEntity<Object> gameAlreadyExistException(GameAlreadyExistException e) {
        log.error("error in " + e.getMethodName());
        BaseResponse errorDetail = BaseResponse.builder().message(e.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        return new ResponseEntity(errorDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}