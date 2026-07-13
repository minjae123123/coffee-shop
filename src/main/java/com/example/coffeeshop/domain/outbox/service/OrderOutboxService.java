package com.example.coffeeshop.domain.outbox.service;

import com.example.coffeeshop.domain.outbox.entity.OrderOutbox;
import com.example.coffeeshop.domain.outbox.repository.OrderOutboxRepository;
import com.example.coffeeshop.infra.dataplatform.DataPlatformSendRequest;
import com.example.coffeeshop.infra.dataplatform.MockDataPlatformClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderOutboxService {

    private final OrderOutboxRepository orderOutboxRepository;
    private final MockDataPlatformClient mockDataPlatformClient;

    @Transactional
    public void send(Long outboxId) {
        OrderOutbox outbox = orderOutboxRepository.findById(outboxId)
                .orElseThrow();

        try {
            mockDataPlatformClient.send(
                    new DataPlatformSendRequest(
                            outbox.getUserId(),
                            outbox.getMenuId(),
                            outbox.getPaymentAmount()
                    )
            );

            outbox.markSent();
        } catch (Exception e) {
            outbox.markFailed();
        }
    }
}