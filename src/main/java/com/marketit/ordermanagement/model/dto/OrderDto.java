package com.marketit.ordermanagement.model.dto;

import com.marketit.ordermanagement.common.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private OrderStatus status;
    private LocalDateTime orderDate;
}
