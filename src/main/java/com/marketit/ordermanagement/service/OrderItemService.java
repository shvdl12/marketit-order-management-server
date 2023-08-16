package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.entity.OrderItem;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ItemService itemService;

    public OrderItem createOrderItem(Order order, CreateOrderItemRequest request) {

        Item item = itemService.getItem(request.getItemId());

        if (item.hasStock(request.getCount())) {
            item.minusStock(request.getCount());
        } else {
            throw new ApiException(ErrorCode.OUT_OF_STOCK, request.getItemId());
        }

        return orderItemRepository.save(OrderItem.builder()
                .order(order)
                .item(item)
                .price(item.getPrice() * request.getCount())
                .count(request.getCount())
                .build());
    }
}
