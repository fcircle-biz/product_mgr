package com.example.productmgr.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TestControllerAdvice {
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleRuntimeException(RuntimeException e) {
        if (e.getMessage() != null && e.getMessage().contains("商品が見つかりません")) {
            // Do nothing, just return 404
        } else {
            throw e;
        }
    }
}