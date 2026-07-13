package com.example.coffeeshop.domain.point.repository;

import com.example.coffeeshop.domain.point.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}