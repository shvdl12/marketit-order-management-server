package com.marketit.ordermanagement.service;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.Address;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.exception.ApiException;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.repository.ItemRepository;
import com.marketit.ordermanagement.repository.MemberRepository;
import com.marketit.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    private Member member;
    private Item item;
    private Order order;

    @BeforeAll
    public void set() {
        member = createMember();
        item = createItem();
        order = createOrder();
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
        orderService.orderComplete(request);

        ApiException exception = assertThrows(ApiException.class,
                () -> orderService.orderComplete(request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.ORDER_ID_NOT_FOUND);
    }




    private Member createMember() {

        Address address = Address.builder()
                .street("서울 성동구 왕십리로 125")
                .detail("패스트파이브 서울숲점 12F 마켓잇")
                .zipCode("04766")
                .build();

        return memberRepository.save(Member.builder()
                .userId("test001")
                .name("홍길동")
                .email("test001@google.com")
                .address(address)
                .build());
    }

    private Item createItem() {
        Item item = new Item("먹태깡", 2000, 10);
        return itemRepository.save(item);
    }

    private Order createOrder() {
        Order order = Order.builder()
                .member(member)
                .status(OrderStatus.ORDERED)
                .build();

        return orderRepository.save(order);
    }
}
