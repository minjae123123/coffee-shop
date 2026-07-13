package com.example.coffeeshop.domain.order.repository;

import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CoffeeOrder, Long> {
}