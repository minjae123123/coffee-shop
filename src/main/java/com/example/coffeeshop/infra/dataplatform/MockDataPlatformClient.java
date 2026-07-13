package com.example.coffeeshop.infra.dataplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockDataPlatformClient {

    public void send(DataPlatformSendRequest request) {
        log.info(
                "데이터 플랫폼 전송 완료 userId={}, menuId={}, paymentAmount={}",
                request.userId(),
                request.menuId(),
                request.paymentAmount()
        );
    }
}