package com.marketit.ordermanagement.exception;

import com.marketit.ordermanagement.common.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException e) {
        return ResponseEntity.ok(CommonResponse.builder()
                .code(ErrorCode.INVALID_PARAMETER.getCode())
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        return ResponseEntity.ok(CommonResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleException(Exception e) {
        log.error("exception occurred", e);
        return ResponseEntity.ok(CommonResponse.builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(e.getMessage())
                .build());
    }
}
