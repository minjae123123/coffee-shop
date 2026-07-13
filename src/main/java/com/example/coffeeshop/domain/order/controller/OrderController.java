package com.example.coffeeshop.domain.order.controller;

import com.example.coffeeshop.domain.order.dto.request.OrderRequest;
import com.example.coffeeshop.domain.order.dto.response.OrderResponse;
import com.example.coffeeshop.domain.order.service.OrderService;
import com.example.coffeeshop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> order(
            @Valid @RequestBody OrderRequest request
    ) {
        OrderResponse response = orderService.order(request);

        return ApiResponse.success("주문 및 결제가 완료되었습니다.", response);
    }
}