package com.marketit.ordermanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompleteOrderRequest {
    private Long orderId;
}
