package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.ItemDto;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.dto.OrderItemDto;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.repository.OrderRepository;
import com.marketit.ordermanagement.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TestUtil testUtil;

    private Member member;
    private Item item;
    private Order order;

    @BeforeAll
    public void set() {
        member = testUtil.createMember();
        item = testUtil.createItem();
        order = testUtil.createOrder(member);
    }

    @Test
    public void create_order_correctly() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(member.getUserId());

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setItemId(item.getId());
        itemRequest.setCount(3);
        request.setOrderItems(Collections.singletonList(itemRequest));

        OrderDto orderDto = orderService.order(request);
        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.ORDERED);
    }

    @Test
    public void create_order_user_not_found() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("invalid_user");

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setItemId(item.getId());
        itemRequest.setCount(3);
        request.setOrderItems(Collections.singletonList(itemRequest));

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.order(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void create_order_item_id_not_found() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(member.getUserId());

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setItemId(-1L);
        itemRequest.setCount(3);
        request.setOrderItems(Collections.singletonList(itemRequest));

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.order(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ITEM_ID_NOT_FOUND);
    }

    @Test
    public void create_order_out_of_stock() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(member.getUserId());

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setItemId(item.getId());
        itemRequest.setCount(item.getStock() + 1);
        request.setOrderItems(Collections.singletonList(itemRequest));

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.order(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.OUT_OF_STOCK);
    }

    @Test
    public void complete_order_correctly() {
        CompleteOrderRequest request = new CompleteOrderRequest(order.getId());
        orderService.orderComplete(request);

        Order completedOrder = orderRepository.getById(order.getId());
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    public void complete_order_order_id_not_found() {
        CompleteOrderRequest request = new CompleteOrderRequest(-1L);

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.orderComplete(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_ID_NOT_FOUND);
    }

    @Test
    public void get_order_correctly() {
        OrderDto orderDto = orderService.getOrder(order.getId(), member.getUserId());
        validateOrderDto(orderDto);
    }

    @Test
    public void get_order_order_id_not_found() {

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.getOrder(-1L, member.getUserId()));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_ID_NOT_FOUND);
    }

    @Test
    void get_order_list_correctly() {
        List<OrderDto> orderDtoList = orderService.getOrderList(member.getUserId());

        assertThat(orderDtoList).hasSize(1);

        OrderDto orderDto = orderDtoList.get(0);
        validateOrderDto(orderDto);
    }


    private void validateOrderDto(OrderDto orderDto) {
        assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.ORDERED);
        assertThat(orderDto.getOrderItem()).isNotEmpty();

        OrderItemDto orderItemDto = orderDto.getOrderItem().get(0);
        ItemDto itemDto = orderItemDto.getItem();

        assertThat(orderItemDto.getCount()).isEqualTo(order.getOrderItems().get(0).getCount());
        assertThat(orderItemDto.getPrice()).isEqualTo(order.getOrderItems().get(0).getPrice());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
        assertThat(itemDto.getPrice()).isEqualTo(item.getPrice());
    }
}

