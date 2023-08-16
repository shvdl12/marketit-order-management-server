package com.marketit.ordermanagement.util;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.*;
import com.marketit.ordermanagement.repository.ItemRepository;
import com.marketit.ordermanagement.repository.MemberRepository;
import com.marketit.ordermanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestUtil {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;


    public Member createMember() {

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

    public Item createItem() {
        Item item = new Item("먹태깡", 2000, 10);
        return itemRepository.save(item);
    }

    public Order createOrder(Member member) {

        Order order = Order.builder()
                .member(member)
                .status(OrderStatus.ORDERED)
                .build();

        Item item = createItem();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .count(2)
                .item(item)
                .price(item.getPrice() * 2)
                .build();

        order.getOrderItems().add(orderItem);

        return orderRepository.save(order);
    }
}
