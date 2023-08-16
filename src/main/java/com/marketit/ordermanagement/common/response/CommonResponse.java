package com.marketit.ordermanagement.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.marketit.ordermanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> CommonResponse<T> ok(T data) {
        return CommonResponse.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .data(data)
                .build();
    }

    public static CommonResponse ok() {
        return CommonResponse.builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }

}
