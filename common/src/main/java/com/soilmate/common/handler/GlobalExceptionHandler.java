package com.soilmate.common.handler;

import com.soilmate.common.exception.AuthenticationException;
import com.soilmate.common.exception.BarcodeException;
import com.soilmate.common.exception.BusinessException;
import com.soilmate.common.exception.ResourceNotFoundException;
import com.soilmate.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handle(BusinessException e){
        return ApiResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handle(AuthenticationException e){
        return ApiResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handle(ResourceNotFoundException e){
        return ApiResponse.error(e.getErrorCode());
    }

    @ExceptionHandler(BarcodeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handle(BarcodeException e){
        return ApiResponse.error(e.getErrorCode());
    }

}
