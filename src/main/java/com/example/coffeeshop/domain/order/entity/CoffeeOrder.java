package com.example.coffeeshop.domain.order.entity;

import com.example.coffeeshop.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_popular", columnList = "status, createdAt, menuId")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoffeeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private Long menuPrice;

    @Column(nullable = false)
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private CoffeeOrder(
            Long userId,
            Long menuId,
            String menuName,
            Long menuPrice,
            Long paymentAmount,
            OrderStatus status
    ) {
        this.userId = userId;
        this.menuId = menuId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.paymentAmount = paymentAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static CoffeeOrder completed(Long userId, Menu menu) {
        return new CoffeeOrder(
                userId,
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getPrice(),
                OrderStatus.COMPLETED
        );
    }
}