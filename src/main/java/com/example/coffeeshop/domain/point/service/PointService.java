package com.example.coffeeshop.domain.point.service;

import com.example.coffeeshop.domain.point.dto.request.PointChargeRequest;
import com.example.coffeeshop.domain.point.dto.response.PointChargeResponse;
import com.example.coffeeshop.domain.point.entity.PointHistory;
import com.example.coffeeshop.domain.point.entity.UserPoint;
import com.example.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.example.coffeeshop.domain.point.repository.UserPointRepository;
import com.example.coffeeshop.domain.user.repository.UserRepository;
import com.example.coffeeshop.global.exception.CustomException;
import com.example.coffeeshop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointChargeResponse charge(PointChargeRequest request) {
        Long userId = request.userId();
        Long amount = request.amount();

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        UserPoint userPoint = userPointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        userPoint.charge(amount);

        pointHistoryRepository.save(
                PointHistory.charge(userId, amount)
        );

        return new PointChargeResponse(
                userId,
                amount,
                userPoint.getPoint()
        );
    }
}