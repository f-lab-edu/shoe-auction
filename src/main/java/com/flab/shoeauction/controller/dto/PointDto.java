package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.point.Point;
import com.flab.shoeauction.domain.point.PointDivision;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WithdrawalRequest {

        private Long withdrawalAmount;
        private String password;

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }

        @Builder
        public WithdrawalRequest(Long withdrawalAmount, String password) {
            this.withdrawalAmount = withdrawalAmount;
            this.password = password;
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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