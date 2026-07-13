package com.example.coffeeshop.infra.dataplatform;

public record DataPlatformSendRequest(
        Long userId,
        Long menuId,
        Long paymentAmount
) {
}