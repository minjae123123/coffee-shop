package com.example.coffeeshop.domain.menu.dto.response;

public record PopularMenuResponse(
        Long menuId,
        String menuName,
        Long orderCount
) {
}