package com.marketit.ordermanagement.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    @NotNull(message = "User ID is required")
    private String userId;
    @Valid
    @Size(min = 1, message = "Order Items must not be empty")
    @NotNull(message = "Order Items is required")
    private List<CreateOrderItemRequest> orderItems;
}
