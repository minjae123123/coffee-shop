package com.example.coffeeshop.domain.point.controller;

import com.example.coffeeshop.domain.point.dto.request.PointChargeRequest;
import com.example.coffeeshop.domain.point.dto.response.PointChargeResponse;
import com.example.coffeeshop.domain.point.service.PointService;
import com.example.coffeeshop.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ApiResponse<PointChargeResponse> charge(
            @Valid @RequestBody PointChargeRequest request
    ) {
        PointChargeResponse response = pointService.charge(request);

        return ApiResponse.success("포인트 충전이 완료되었습니다.", response);
    }
}