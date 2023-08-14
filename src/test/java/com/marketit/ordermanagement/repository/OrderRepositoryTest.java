package com.marketit.ordermanagement.repository;

import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void create_order() {

        Member member = createMember();
        Item item = createItem();

        Order order = orderRepository.save(Order.builder()
                .status(OrderStatus.ORDERED)
                .member(member)
                .build());

        OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
                .item(item)
                .price(2000)
                .count(2)
                .order(order)
                .build());

        assertThat(order.getMember().getId()).isEqualTo(member.getId());
        assertThat(orderItem.getOrder().getId()).isEqualTo(order.getId());
        assertThat(orderItem.getItem().getId()).isEqualTo(item.getId());
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
}
