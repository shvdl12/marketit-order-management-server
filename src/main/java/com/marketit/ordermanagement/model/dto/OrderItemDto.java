package com.marketit.ordermanagement.model.dto;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.Item;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class OrderItemDto {
    private ItemDto item;
    private int count;
    private int price;
}
