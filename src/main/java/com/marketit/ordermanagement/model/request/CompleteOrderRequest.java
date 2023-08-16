package com.marketit.ordermanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
}
