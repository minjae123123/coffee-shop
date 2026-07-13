package com.example.coffeeshop.domain.point.dto.response;

public record PointChargeResponse(
        Long userId,
        Long chargedAmount,
        Long currentPoint
) {
}