package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.entity.OrderItem;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.ItemDto;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.dto.OrderItemDto;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.repository.ItemRepository;
import com.marketit.ordermanagement.repository.MemberRepository;
import com.marketit.ordermanagement.repository.OrderItemRepository;
import com.marketit.ordermanagement.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
            order.getOrderItems().add(orderItem);
        }

        return convertOrderDto(order);
    }

    @Transactional
    public void orderComplete(CompleteOrderRequest completeOrderRequest) {
        Order order = orderRepository.findById(completeOrderRequest.getOrderId())
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_ID_NOT_FOUND, completeOrderRequest.getOrderId()));

        order.updateOrderStatus(OrderStatus.COMPLETED);
    }

    public OrderDto getOrder(Long orderId, String userId) {
        Order order = orderRepository.findByIdAndMemberUserId(orderId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_ID_NOT_FOUND, orderId));

        return convertOrderDto(order);
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

        return orderItemRepository.save(OrderItem.builder()
                .order(order)
                .item(item)
                .price(item.getPrice() * request.getCount())
                .count(request.getCount())
                .build());
    }

    private OrderDto convertOrderDto(Order order) {
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);

        List<OrderItemDto> orderItemDtoList = order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemDto orderItemDto = modelMapper.map(orderItem, OrderItemDto.class);
                    orderItemDto.setItem(modelMapper.map(orderItem.getItem(), ItemDto.class));
                    return orderItemDto;
                })
                .collect(Collectors.toList());

        orderDto.setOrderItem(orderItemDtoList);

        return orderDto;
    }
}
