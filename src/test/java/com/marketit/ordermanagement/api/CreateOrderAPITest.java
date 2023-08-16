package com.marketit.ordermanagement.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketit.ordermanagement.common.model.OrderStatus;
import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.entity.Item;
import com.marketit.ordermanagement.entity.Member;
import com.marketit.ordermanagement.entity.Order;
import com.marketit.ordermanagement.exception.ErrorCode;
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
public class CreateOrderAPITest {
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
    public void create_order_missing_user_id() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderItems(Arrays.asList(new CreateOrderItemRequest(1L, 1)));

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode(), "User ID is required");
    }

    @Test
    public void create_order_missing_order_item() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("userId");

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode(), "Order Items is required");
    }

    @Test
    public void create_order_empty_order_item() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("userId");
        request.setOrderItems(new ArrayList<>());

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode()
                , "Order Items must not be empty");
    }

    @Test
    public void create_order_missing_item_id() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("userId");

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setCount(1);

        request.setOrderItems(Arrays.asList(itemRequest));

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode()
                , "Item ID is required");
    }

    @Test
    public void create_order_count_less_than_1() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("userId");

        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setItemId(1L);
        itemRequest.setCount(0);

        request.setOrderItems(Arrays.asList(itemRequest));

        validateErrorResponse(request, ErrorCode.INVALID_PARAMETER.getCode()
                , "Count must be at least 1");
    }

    @Test
    public void create_order_user_not_found() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId("invalidUserId");
        request.setOrderItems(Arrays.asList(new CreateOrderItemRequest(1L, 1)));

        validateErrorResponse(request, ErrorCode.USER_NOT_FOUND.getCode()
                , ErrorCode.USER_NOT_FOUND.getMessage() + ": invalidUserId");
    }

    @Test
    public void create_order_item_id_not_found() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(member.getUserId());
        request.setOrderItems(Arrays.asList(new CreateOrderItemRequest(-1L, 1)));


        validateErrorResponse(request, ErrorCode.ITEM_ID_NOT_FOUND.getCode(),
                ErrorCode.ITEM_ID_NOT_FOUND.getMessage() + ": -1");
    }

    @Test
    public void create_order_correctly() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        int orderCount = 2;
        request.setUserId(member.getUserId());
        request.setOrderItems(Arrays.asList(new CreateOrderItemRequest(item.getId(), orderCount)));

        mockMvc.perform(MockMvcRequestBuilders.post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    String json = response.getContentAsString();
                    CommonResponse<OrderDto> commonResponse = objectMapper.readValue(json,
                            new TypeReference<CommonResponse<OrderDto>>() {
                            });

                    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
                    assertThat(commonResponse.getCode()).isEqualTo(ErrorCode.SUCCESS.getCode());

                    OrderDto orderDto = commonResponse.getData();
                    OrderItemDto orderItemDto = commonResponse.getData().getOrderItem().get(0);

                    assertThat(orderDto.getId()).isNotNull();
                    assertThat(orderDto.getStatus()).isEqualTo(OrderStatus.ORDERED);
                    assertThat(orderItemDto.getCount()).isEqualTo(orderCount);
                    assertThat(orderItemDto.getPrice()).isEqualTo(item.getPrice() * orderCount);
                    assertThat(orderItemDto.getItem().getName()).isEqualTo(item.getName());
                    assertThat(orderItemDto.getItem().getPrice()).isEqualTo(item.getPrice());
                });
    }

    private void validateErrorResponse(CreateOrderRequest request, String expectedCode, String expectedMessage)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/order/create")
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
