package com.marketit.ordermanagement.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank
    private String userId;
    @NotNull
    private List<CreateOrderItemRequest> orderItems;
}
