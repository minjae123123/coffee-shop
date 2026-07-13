package com.example.coffeeshop.domain.menu.service;

import com.example.coffeeshop.domain.menu.dto.MenuResponse;
import com.example.coffeeshop.domain.menu.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus() {
        return menuRepository.findAll()
                .stream()
                .map(MenuResponse::from)
                .toList();
    }
}