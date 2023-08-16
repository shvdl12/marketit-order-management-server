package com.marketit.ordermanagement.controller;

import com.marketit.ordermanagement.common.response.CommonResponse;
import com.marketit.ordermanagement.model.dto.OrderDto;
import com.marketit.ordermanagement.model.request.CompleteOrderRequest;
import com.marketit.ordermanagement.model.request.CreateOrderRequest;
import com.marketit.ordermanagement.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    @GetMapping("/list/{userId}")
    public ResponseEntity<CommonResponse<List<OrderDto>>> getOrderList(@PathVariable String userId) {
        List<OrderDto> orderDtoList = orderService.getOrderList(userId);
        return ResponseEntity.ok(CommonResponse.ok(orderDtoList));
    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<CommonResponse<OrderDto>> getOrder(@PathVariable String userId, @PathVariable Long orderId) {
        OrderDto orderDto = orderService.getOrder(orderId, userId);
        return ResponseEntity.ok(CommonResponse.ok(orderDto));
    }

    @PostMapping("/create")
    public ResponseEntity<CommonResponse<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto orderDto = orderService.order(request);
        return ResponseEntity.ok(CommonResponse.ok(orderDto));
    }

    @PostMapping("/complete")
    public ResponseEntity<CommonResponse> completeOrder(@Valid @RequestBody CompleteOrderRequest request) {
        orderService.orderComplete(request);
        return ResponseEntity.ok(CommonResponse.ok());
    }
}
