package com.marketit.ordermanagement.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.dto.MemberDto;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.dto.OrderItemDto;
import com.marketit.ordermanagement.model.request.CreateOrderItemRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.util.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FetchMemberAPITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestUtil testUtil;

    private Member member;

    @BeforeAll
    public void set() {
        member = testUtil.createMember();
    }

    @Test
    public void fetch_member_correctly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/member/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    String json = response.getContentAsString();
                    CommonResponse<MemberDto> commonResponse = objectMapper.readValue(json,
                            new TypeReference<CommonResponse<MemberDto>>() {
                            });

                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commonResponse.getCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
                    assertThat(commonResponse.getData().getUserId()).isEqualTo(member.getUserId());
                    assertThat(commonResponse.getData().getName()).isEqualTo(member.getName());
                });
    }
}
