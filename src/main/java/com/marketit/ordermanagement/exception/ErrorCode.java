package com.marketit.ordermanagement.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS("0000", "success"),
    INVALID_PARAMETER("1000", "Invalid parameter"),
    USER_NOT_FOUND("2000", "User not found"),
    ITEM_ID_NOT_FOUND("2001", "Item id not found"),
    OUT_OF_STOCK("2002", "Out of stock"),
    ORDER_ID_NOT_FOUND("2003", "Order id not found"),
    INTERNAL_SERVER_ERROR("9999", "Internal server error");


    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
