package com.example.coffeeshop.domain.point.entity;

import com.example.coffeeshop.global.common.BaseTimeEntity;
import com.example.coffeeshop.global.exception.CustomException;
import com.example.coffeeshop.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Long point;

    public UserPoint(Long userId, Long point) {
        this.userId = userId;
        this.point = point;
    }

    public void charge(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CustomException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        this.point += amount;
    }

    public void use(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CustomException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        if (this.point < amount) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINT);
        }

        this.point -= amount;
    }
}