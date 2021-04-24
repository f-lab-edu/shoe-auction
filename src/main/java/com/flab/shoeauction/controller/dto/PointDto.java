package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.point.Point;
import com.flab.shoeauction.domain.point.PointDivision;
import com.flab.shoeauction.domain.users.user.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointDto {

    @Getter
    @NoArgsConstructor
    public static class ChargeRequest {

        private Long chargeAmount;

        @Builder
        public ChargeRequest(Long chargeAmount) {
            this.chargeAmount = chargeAmount;
        }

        public Point toEntity(User user) {
            return Point.builder()
                .amount(chargeAmount)
                .user(user)
                .division(PointDivision.CHARGE)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class WithdrawalRequest {
        private Long withdrawalAmount;

        @Builder
        public WithdrawalRequest(Long withdrawalAmount) {
            this.withdrawalAmount = withdrawalAmount;
        }

        public Point toEntity(User user) {
            return Point.builder()
                .amount(withdrawalAmount)
                .user(user)
                .division(PointDivision.WITHDRAW)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PointHistoryDto {
        private LocalDateTime time;
        private Long amount;
        private PointDivision division;

        @Builder
        public PointHistoryDto(LocalDateTime time, Long amount,
            PointDivision division) {
            this.time = time;
            this.amount = amount;
            this.division = division;
        }
    }

}