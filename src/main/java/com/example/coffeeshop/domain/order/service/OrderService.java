package com.example.coffeeshop.domain.order.service;

import com.example.coffeeshop.domain.menu.entity.Menu;
import com.example.coffeeshop.domain.menu.repository.MenuRepository;
import com.example.coffeeshop.domain.order.dto.request.OrderRequest;
import com.example.coffeeshop.domain.order.dto.response.OrderResponse;
import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import com.example.coffeeshop.domain.order.repository.OrderRepository;
import com.example.coffeeshop.domain.outbox.entity.OrderOutbox;
import com.example.coffeeshop.domain.outbox.repository.OrderOutboxRepository;
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
public class OrderService {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;

    @Transactional
    public OrderResponse order(OrderRequest request) {
        Long userId = request.userId();
        Long menuId = request.menuId();

        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));

        UserPoint userPoint = userPointRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_NOT_FOUND));

        Long paymentAmount = menu.getPrice();

        userPoint.use(paymentAmount);

        CoffeeOrder order = orderRepository.save(
                CoffeeOrder.completed(userId, menu)
        );

        pointHistoryRepository.save(
                PointHistory.use(userId, paymentAmount)
        );

        orderOutboxRepository.save(
                OrderOutbox.from(order)
        );

        return OrderResponse.of(order, userPoint.getPoint());
    }
}