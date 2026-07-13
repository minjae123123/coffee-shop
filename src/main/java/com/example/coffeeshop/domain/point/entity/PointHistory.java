package com.example.coffeeshop.domain.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointHistoryType type;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PointHistory(Long userId, Long amount, PointHistoryType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public static PointHistory charge(Long userId, Long amount) {
        return new PointHistory(userId, amount, PointHistoryType.CHARGE);
    }

    public static PointHistory use(Long userId, Long amount) {
        return new PointHistory(userId, amount, PointHistoryType.USE);
    }
}