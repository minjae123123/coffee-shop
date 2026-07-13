package com.example.coffeeshop.domain.order.repository;

import com.example.coffeeshop.domain.menu.dto.response.PopularMenuResponse;
import com.example.coffeeshop.domain.order.entity.CoffeeOrder;
import com.example.coffeeshop.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<CoffeeOrder, Long> {

    @Query("""
            select new com.example.coffeeshop.domain.menu.dto.response.PopularMenuResponse(
                o.menuId,
                o.menuName,
                count(o.id)
            )
            from CoffeeOrder o
            where o.status = :status
              and o.createdAt >= :startDate
            group by o.menuId, o.menuName
            order by count(o.id) desc, o.menuId asc
            """)
    List<PopularMenuResponse> findPopularMenus(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}