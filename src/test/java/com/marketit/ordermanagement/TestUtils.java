package com.marketit.ordermanagement;

import com.marketit.ordermanagement.entity.Address;
import com.marketit.ordermanagement.entity.Member;

public class TestUtils {

    public static Address getAddress() {
        return Address.builder()
                .street("서울 성동구 왕십리로 125")
                .detail("패스트파이브 서울숲점 12F 마켓잇")
                .zipCode("04766")
                .build();
    }

    public static Member getMember() {
        return Member.builder()
                .name("홍길동")
                .email("account@google.com")
                .address(getAddress())
                .build();
    }
}
