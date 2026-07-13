package com.example.coffeeshop.domain.menu.service;

import com.example.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.example.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeshop.domain.menu.repository.MenuRepository;
import com.example.coffeeshop.domain.order.entity.OrderStatus;
import com.example.coffeeshop.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularMenuResponse> getPopularMenus() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        return orderRepository.findPopularMenus(
                OrderStatus.COMPLETED,
                sevenDaysAgo,
                PageRequest.of(0, 3)
        );
    }
}