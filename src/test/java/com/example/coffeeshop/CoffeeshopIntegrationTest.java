package com.example.coffeeshop;

import com.example.coffeeshop.domain.menu.dto.response.MenuResponse;
import com.example.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeshop.domain.menu.entity.Menu;
import com.example.coffeeshop.domain.menu.repository.MenuRepository;
import com.example.coffeeshop.domain.menu.service.MenuService;
import com.example.coffeeshop.domain.order.dto.request.OrderRequest;
import com.example.coffeeshop.domain.order.dto.response.OrderResponse;
import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import com.example.coffeeshop.domain.order.entity.OrderStatus;
import com.example.coffeeshop.domain.order.repository.OrderRepository;
import com.example.coffeeshop.domain.order.service.OrderService;
import com.example.coffeeshop.domain.outbox.entity.OrderOutbox;
import com.example.coffeeshop.domain.outbox.entity.OutboxStatus;
import com.example.coffeeshop.domain.outbox.repository.OrderOutboxRepository;
import com.example.coffeeshop.domain.outbox.service.OrderOutboxService;
import com.example.coffeeshop.domain.point.dto.request.PointChargeRequest;
import com.example.coffeeshop.domain.point.dto.response.PointChargeResponse;
import com.example.coffeeshop.domain.point.entity.UserPoint;
import com.example.coffeeshop.domain.point.repository.PointHistoryRepository;
import com.example.coffeeshop.domain.point.repository.UserPointRepository;
import com.example.coffeeshop.domain.point.service.PointService;
import com.example.coffeeshop.domain.user.entity.User;
import com.example.coffeeshop.domain.user.repository.UserRepository;
import com.example.coffeeshop.global.exception.CustomException;
import com.example.coffeeshop.global.exception.ErrorCode;
import com.example.coffeeshop.infra.dataplatform.MockDataPlatformClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class CoffeeshopIntegrationTest {

    @MockitoBean
    MockDataPlatformClient mockDataPlatformClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    UserPointRepository userPointRepository;

    @Autowired
    PointHistoryRepository pointHistoryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderOutboxRepository orderOutboxRepository;

    @Autowired
    MenuService menuService;

    @Autowired
    PointService pointService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderOutboxService orderOutboxService;

    @BeforeEach
    void setUp() {
        Mockito.reset(mockDataPlatformClient);

        orderOutboxRepository.deleteAll();
        pointHistoryRepository.deleteAll();
        orderRepository.deleteAll();
        userPointRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 커피_메뉴_목록을_조회한다() {
        // given
        menuRepository.save(new Menu("아메리카노", 4500L));
        menuRepository.save(new Menu("카페라떼", 5000L));

        // when
        List<MenuResponse> result = menuService.getMenus();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("아메리카노");
        assertThat(result.get(0).price()).isEqualTo(4500L);
    }

    @Test
    void 포인트를_충전한다() {
        // given
        User user = userRepository.save(new User("민재"));
        userPointRepository.save(new UserPoint(user.getId(), 0L));

        // when
        PointChargeResponse response = pointService.charge(
                new PointChargeRequest(user.getId(), 10000L)
        );

        // then
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.chargedAmount()).isEqualTo(10000L);
        assertThat(response.currentPoint()).isEqualTo(10000L);

        UserPoint userPoint = userPointRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(userPoint.getPoint()).isEqualTo(10000L);

        assertThat(pointHistoryRepository.findAll()).hasSize(1);
    }

    @Test
    void 포인트가_부족하면_주문에_실패한다() {
        // given
        User user = userRepository.save(new User("민재"));
        userPointRepository.save(new UserPoint(user.getId(), 1000L));
        Menu menu = menuRepository.save(new Menu("아메리카노", 4500L));

        // when & then
        assertThatThrownBy(() -> orderService.order(new OrderRequest(user.getId(), menu.getId())))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_ENOUGH_POINT);

        assertThat(orderRepository.findAll()).isEmpty();

        UserPoint userPoint = userPointRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(userPoint.getPoint()).isEqualTo(1000L);
    }

    @Test
    void 주문_결제에_성공하면_포인트가_차감되고_주문과_Outbox가_저장된다() {
        // given
        User user = userRepository.save(new User("민재"));
        userPointRepository.save(new UserPoint(user.getId(), 10000L));
        Menu menu = menuRepository.save(new Menu("아메리카노", 4500L));

        // when
        OrderResponse response = orderService.order(new OrderRequest(user.getId(), menu.getId()));

        // then
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.menuId()).isEqualTo(menu.getId());
        assertThat(response.paymentAmount()).isEqualTo(4500L);
        assertThat(response.remainingPoint()).isEqualTo(5500L);
        assertThat(response.status()).isEqualTo(OrderStatus.COMPLETED);

        UserPoint userPoint = userPointRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(userPoint.getPoint()).isEqualTo(5500L);

        assertThat(orderRepository.findAll()).hasSize(1);
        assertThat(pointHistoryRepository.findAll()).hasSize(1);
        assertThat(orderOutboxRepository.findAll()).hasSize(1);
    }

    @Test
    void 최근_7일간_인기_메뉴_TOP3를_조회한다() {
        // given
        User user = userRepository.save(new User("민재"));

        Menu americano = menuRepository.save(new Menu("아메리카노", 4500L));
        Menu latte = menuRepository.save(new Menu("카페라떼", 5000L));
        Menu vanilla = menuRepository.save(new Menu("바닐라라떼", 5500L));
        Menu cappuccino = menuRepository.save(new Menu("카푸치노", 5000L));

        saveOrder(user.getId(), americano);
        saveOrder(user.getId(), americano);
        saveOrder(user.getId(), americano);

        saveOrder(user.getId(), latte);
        saveOrder(user.getId(), latte);

        saveOrder(user.getId(), vanilla);

        CoffeeOrder oldOrder = CoffeeOrder.completed(user.getId(), cappuccino);
        ReflectionTestUtils.setField(oldOrder, "createdAt", LocalDateTime.now().minusDays(8));
        orderRepository.save(oldOrder);

        // when
        List<PopularMenuResponse> result = menuService.getPopularMenus();

        // then
        assertThat(result).hasSize(3);

        assertThat(result.get(0).menuId()).isEqualTo(americano.getId());
        assertThat(result.get(0).orderCount()).isEqualTo(3L);

        assertThat(result.get(1).menuId()).isEqualTo(latte.getId());
        assertThat(result.get(1).orderCount()).isEqualTo(2L);

        assertThat(result.get(2).menuId()).isEqualTo(vanilla.getId());
        assertThat(result.get(2).orderCount()).isEqualTo(1L);
    }

    @Test
    void Outbox_전송에_성공하면_SENT_상태가_된다() {
        // given
        User user = userRepository.save(new User("민재"));
        Menu menu = menuRepository.save(new Menu("아메리카노", 4500L));
        CoffeeOrder order = orderRepository.save(CoffeeOrder.completed(user.getId(), menu));

        OrderOutbox outbox = orderOutboxRepository.save(OrderOutbox.from(order));

        // when
        orderOutboxService.send(outbox.getId());

        // then
        OrderOutbox foundOutbox = orderOutboxRepository.findById(outbox.getId()).orElseThrow();

        assertThat(foundOutbox.getStatus()).isEqualTo(OutboxStatus.SENT);
        verify(mockDataPlatformClient).send(any());
    }

    @Test
    void Outbox_전송에_실패하면_FAILED_상태가_된다() {
        // given
        User user = userRepository.save(new User("민재"));
        Menu menu = menuRepository.save(new Menu("아메리카노", 4500L));
        CoffeeOrder order = orderRepository.save(CoffeeOrder.completed(user.getId(), menu));

        OrderOutbox outbox = orderOutboxRepository.save(OrderOutbox.from(order));

        doThrow(new RuntimeException("전송 실패"))
                .when(mockDataPlatformClient)
                .send(any());

        // when
        orderOutboxService.send(outbox.getId());

        // then
        OrderOutbox foundOutbox = orderOutboxRepository.findById(outbox.getId()).orElseThrow();

        assertThat(foundOutbox.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(foundOutbox.getRetryCount()).isEqualTo(1);
    }

    @Test
    void 동시에_주문해도_포인트가_중복_차감되지_않는다() throws InterruptedException {
        // given
        User user = userRepository.save(new User("민재"));
        userPointRepository.save(new UserPoint(user.getId(), 5000L));
        Menu menu = menuRepository.save(new Menu("아메리카노", 4500L));

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    orderService.order(new OrderRequest(user.getId(), menu.getId()));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        UserPoint userPoint = userPointRepository.findByUserId(user.getId()).orElseThrow();

        long completedOrderCount = orderRepository.findAll()
                .stream()
                .filter(order -> order.getUserId().equals(user.getId()))
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .count();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(userPoint.getPoint()).isEqualTo(500L);
        assertThat(completedOrderCount).isEqualTo(1L);
    }

    private void saveOrder(Long userId, Menu menu) {
        orderRepository.save(CoffeeOrder.completed(userId, menu));
    }
}