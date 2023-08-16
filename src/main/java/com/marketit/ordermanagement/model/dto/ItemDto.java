package com.marketit.ordermanagement.model.dto;

import com.marketit.ordermanagement.common.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class ItemDto {
    private String name;
    private int price;
}
