package com.example.coffeeshop.domain.outbox.entity;

import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "order_outboxes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private OrderOutbox(
            Long orderId,
            Long userId,
            Long menuId,
            Long paymentAmount
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.menuId = menuId;
        this.paymentAmount = paymentAmount;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public static OrderOutbox from(CoffeeOrder order) {
        return new OrderOutbox(
                order.getId(),
                order.getUserId(),
                order.getMenuId(),
                order.getPaymentAmount()
        );
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = OutboxStatus.FAILED;
        this.retryCount++;
    }
}