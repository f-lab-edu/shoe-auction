package com.flab.shoeauction.service;

import com.flab.shoeauction.controller.dto.PointDto;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;

    @Transactional
    public void charging(String email, PointDto requestDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        user.chargingPoint(requestDto.getChargeAmount());
    }
}
