package com.example.coffeeshop.domain.outbox.event;

import com.example.coffeeshop.domain.outbox.service.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderOutboxEventListener {

    private final OrderOutboxService orderOutboxService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderOutboxCreatedEvent event) {
        orderOutboxService.send(event.outboxId());
    }
}