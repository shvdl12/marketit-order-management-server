package com.marketit.ordermanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.exception.ErrorCode;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompleteOrderAPITest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
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
    public void complete_order_missing_order_id() throws Exception {
        CompleteOrderRequest request = new CompleteOrderRequest();

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode()
                , "Order ID is required");
    }

    @Test
    public void complete_order_order_id_not_found() throws Exception {
        CompleteOrderRequest request = new CompleteOrderRequest(-1L);

        validateErrorResponse(request, ErrorCode.ORDER_ID_NOT_FOUND.getCode()
                , "Order id not found: -1");
    }

    @Test
    public void complete_order_correctly() throws Exception {
        CompleteOrderRequest request = new CompleteOrderRequest(order.getId());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    CommonResponse commonResponse = objectMapper.readValue(response.getContentAsString()
                            , CommonResponse.class);

                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commonResponse.getCode()).isEqualTo(ErrorCode.SUCCESS.getCode());
                });
    }

    private void validateErrorResponse(CompleteOrderRequest request, String expectedCode, String expectedMessage)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/order/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    CommonResponse commonResponse = objectMapper.readValue(response.getContentAsString()
                            , CommonResponse.class);

                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commonResponse.getCode()).isEqualTo(expectedCode);
                    assertThat(commonResponse.getMessage()).isEqualTo(expectedMessage);
                });
    }
}
