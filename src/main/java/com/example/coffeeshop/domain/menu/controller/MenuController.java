package com.example.coffeeshop.domain.menu.controller;

import com.example.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.example.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeshop.domain.menu.service.MenuService;
import com.example.coffeeshop.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ApiResponse<List<MenuResponse>> getMenus() {
        List<MenuResponse> response = menuService.getMenus();

        return ApiResponse.success("커피 메뉴 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/popular")
    public ApiResponse<List<PopularMenuResponse>> getPopularMenus() {
        List<PopularMenuResponse> response = menuService.getPopularMenus();

        return ApiResponse.success("인기 메뉴 목록 조회에 성공했습니다.", response);
    }
}