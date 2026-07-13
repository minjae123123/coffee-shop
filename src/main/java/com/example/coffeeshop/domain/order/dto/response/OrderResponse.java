package com.example.coffeeshop.domain.order.dto.response;

import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import com.example.coffeeshop.domain.order.entity.OrderStatus;

public record OrderResponse(
        Long orderId,
        Long userId,
        Long menuId,
        String menuName,
        Long paymentAmount,
        Long remainingPoint,
        OrderStatus status
) {

    public static OrderResponse of(CoffeeOrder order, Long remainingPoint) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getMenuId(),
                order.getMenuName(),
                order.getPaymentAmount(),
                remainingPoint,
                order.getStatus()
        );
    }
}