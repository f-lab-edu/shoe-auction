package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.controller.dto.PointDto.ChargeRequest;
import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.controller.dto.PointDto.WithdrawalRequest;
import com.flab.shoeauction.service.PointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("points")
public class PointApiController {

    private final PointService pointService;

    @GetMapping
    public Long getPoint(@CurrentUser String email) {
        return pointService.getUserPoint(email);
    }

    @PostMapping("/charging")
    public void payment(@CurrentUser String email, @RequestBody ChargeRequest requestDto) {
        pointService.charging(email, requestDto);
    }

    @PostMapping("/withdrawal")
    public void withdrawal(@CurrentUser String email, @RequestBody WithdrawalRequest requestDto) {
        pointService.withdrawal(email, requestDto);
    }

    @GetMapping("/deduction-details")
    @ResponseBody
    public List<PointHistoryDto> deductionHistory(@CurrentUser String email) {
        return pointService.getDeductionHistory(email);
    }

    @GetMapping("/charging-details")
    public List<PointHistoryDto> chargingHistory(@CurrentUser String email) {
        return pointService.getChargingHistory(email);
    }
}