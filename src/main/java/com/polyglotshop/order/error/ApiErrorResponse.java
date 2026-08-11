package com.polyglotshop.order.error;

public record ApiErrorResponse(ErrorDetail error) {

    public static ApiErrorResponse of(String code, String message, String requestId) {
        return new ApiErrorResponse(new ErrorDetail(code, message, requestId));
    }

    public record ErrorDetail(String code, String message, String requestId) {
    }
}
