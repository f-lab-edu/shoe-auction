package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.PointDto.ChargeRequest;
import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.controller.dto.PointDto.WithdrawalRequest;
import com.flab.shoeauction.domain.point.PointRepository;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Transactional
    public void charging(String email, ChargeRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        pointRepository.save(requestDto.toEntity(user));

        user.chargingPoint(requestDto.getChargeAmount());
    }

    @Transactional
    public void withdrawal(String email, WithdrawalRequest requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        pointRepository.save(requestDto.toEntity(user));

        user.deductionOfPoints(requestDto.getWithdrawalAmount());
    }

    public List<PointHistoryDto> getDeductionHistory(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getDeductionHistory();
    }

    public List<PointHistoryDto> getChargingHistory(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getChargingHistory();
    }

    public Long getUserPoint(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getPoint();
    }
}