package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.entity.OrderItem;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.repository.ItemRepository;
import com.marketit.ordermanagement.repository.MemberRepository;
import com.marketit.ordermanagement.repository.OrderItemRepository;
import com.marketit.ordermanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Transactional
    public OrderDto order(CreateOrderRequest createOrderRequest) {

        Order order = createOrder(createOrderRequest.getUserId());

        for (CreateOrderItemRequest itemRequest : createOrderRequest.getOrderItems()) {
            OrderItem orderItem = createOrderItem(order, itemRequest);
            orderItemRepository.save(orderItem);
        }

        return modelMapper.map(order, OrderDto.class);
    }


    private Order createOrder(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND, userId));

        return orderRepository.save(Order.builder()
                .member(member)
                .status(OrderStatus.ORDERED)
                .build());
    }

    private OrderItem createOrderItem(Order order, CreateOrderItemRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_ID_NOT_FOUND, request.getItemId()));

        if (item.hasStock(request.getCount())) {
            item.minusStock(request.getCount());
        } else {
            throw new ApiException(ErrorCode.OUT_OF_STOCK, request.getItemId());
        }

        return OrderItem.builder()
                .order(order)
                .item(item)
                .price(item.getPrice() * request.getCount())
                .count(request.getCount())
                .build();
    }
}
