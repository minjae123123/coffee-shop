package com.example.coffeeshop.global.response;

import com.example.coffeeshop.global.exception.ErrorCode;

public record ErrorResponse(
        boolean success,
        String errorCode,
        String message
) {

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(
                false,
                errorCode.name(),
                errorCode.getMessage()
        );
    }
}