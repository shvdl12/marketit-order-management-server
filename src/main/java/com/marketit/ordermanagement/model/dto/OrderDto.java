package com.marketit.ordermanagement.model.dto;

import com.marketit.ordermanagement.common.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private List<OrderItemDto> orderItem;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
