package com.marketit.ordermanagement.controller;

import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.model.response.CreateOrderResponse;
import com.marketit.ordermanagement.model.response.FetchOrderListResponse;
import com.marketit.ordermanagement.model.response.FetchOrderResponse;
import com.marketit.ordermanagement.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @GetMapping(path = "/list/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "주문 목록 조회", description = "주문 목록 조회 API")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FetchOrderListResponse.class)),
            description = "유저의 ID를 입력 받아 해당 유저의 주문 리스트를 반환합니다.")
    public ResponseEntity<CommonResponse<List<OrderDto>>> getOrderList(@PathVariable String userId) {
        List<OrderDto> orderDtoList = orderService.getOrderList(userId);
        return ResponseEntity.ok(CommonResponse.ok(orderDtoList));
    }

    @GetMapping(path = "/{userId}/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "주문 조회", description = "주문 조회 API")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = FetchOrderResponse.class)),
            description = "유저의 ID와 주문 번호를 입력 받아 해당 주문 정보를 반환합니다.")
    public ResponseEntity<CommonResponse<OrderDto>> getOrder(@PathVariable String userId, @PathVariable Long orderId) {
        OrderDto orderDto = orderService.getOrder(orderId, userId);
        return ResponseEntity.ok(CommonResponse.ok(orderDto));
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "주문 접수", description = "주문 접수 API")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CreateOrderResponse.class)),
            description = "유저의 ID와 주문 정보를 입력 받아 주문을 생성하고 주문 정보를 반환합니다.")
    public ResponseEntity<CommonResponse<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto orderDto = orderService.order(request);
        return ResponseEntity.ok(CommonResponse.ok(orderDto));
    }

    @PostMapping(path = "/complete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "주문 완료 처리", description = "주문 완료 처리 API")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CommonResponse.class)),
            description = "주문 번호를 입력 받아 주문의 상태를 변경합니다.")
    public ResponseEntity<CommonResponse> completeOrder(@Valid @RequestBody CompleteOrderRequest request) {
        orderService.orderComplete(request);
        return ResponseEntity.ok(CommonResponse.ok());
    }
}
