package com.marketit.ordermanagement.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("0000", "success"),
    INVALID_PARAMETER("1000", "Invalid parameter"),
    USER_NOT_FOUND("2000", "User not found"),
    ITEM_ID_NOT_FOUND("2000", "User not found"),
    OUT_OF_STOCK("2000", "Out of stock"),
    ORDER_ID_NOT_FOUND("2000", "Order id not found"),

    ;

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}