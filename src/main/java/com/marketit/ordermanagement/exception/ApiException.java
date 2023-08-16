package com.marketit.ordermanagement.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ErrorCode errorCode;

    public ApiException(ErrorCode errorCode, String data) {
        super(errorCode.getMessage() + ": " + data);
        this.errorCode = errorCode;
    }

    public ApiException(ErrorCode errorCode, Long data) {
        super(errorCode.getMessage() + ": " + data);
        this.errorCode = errorCode;
    }

}
