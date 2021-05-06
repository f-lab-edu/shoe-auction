package com.flab.shoeauction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.shoeauction.controller.dto.PointDto.ChargeRequest;
import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.controller.dto.PointDto.WithdrawalRequest;
import com.flab.shoeauction.domain.addressBook.AddressBook;
import com.flab.shoeauction.domain.point.Point;
import com.flab.shoeauction.domain.point.PointDivision;
import com.flab.shoeauction.domain.point.PointRepository;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.domain.users.common.UserStatus;
import com.flab.shoeauction.domain.users.user.User;
import com.flab.shoeauction.domain.users.user.UserRepository;
import com.flab.shoeauction.exception.user.UserNotFoundException;
import com.flab.shoeauction.service.encrytion.EncryptionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private PointService pointService;

    private User createUser() {
        return User.builder()
            .id(10L)
            .email("test123@test.com")
            .password("test1234")
            .phone("01011112222")
            .nickname("17171771")
            .nicknameModifiedDate(LocalDateTime.now())
            .userLevel(UserLevel.AUTH)
            .userStatus(UserStatus.NORMAL)
            .addressBook(new AddressBook())
            .point(50000L)
            .pointBreakdown(createPointBreakDownMockData())
            .build();
    }

    private List<Point> createPointBreakDownMockData() {
        User tempUser = User.builder()
            .id(10L)
            .email("tempUser@test.com")
            .password("test1234")
            .phone("01033335555")
            .nickname("임시유저")
            .nicknameModifiedDate(LocalDateTime.now())
            .userLevel(UserLevel.AUTH)
            .userStatus(UserStatus.NORMAL)
            .addressBook(new AddressBook())
            .point(50000L)
            .build();

        List<Point> pointBreakDownMockDataList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Point point = Point.builder()
                .amount(100000L)
                .division(PointDivision.CHARGE)
                .user(tempUser)
                .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 5; i++) {
            Point point = Point.builder()
                .amount(100000L)
                .division(PointDivision.SALES_REVENUE)
                .user(tempUser)
                .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 3; i++) {
            Point point = Point.builder()
                .amount(100000L)
                .division(PointDivision.WITHDRAW)
                .user(tempUser)
                .build();

            pointBreakDownMockDataList.add(point);
        }

        for (int i = 0; i < 3; i++) {
            Point point = Point.builder()
                .amount(100000L)
                .division(PointDivision.PURCHASE_DEDUCTION)
                .user(tempUser)
                .build();

            pointBreakDownMockDataList.add(point);
        }

        return pointBreakDownMockDataList;
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    public void charging() {
        User user = createUser();
        Long nowPoint = user.getPoint();
        String email = "test123@test.com";
        ChargeRequest chargeRequest = ChargeRequest.builder()
            .chargeAmount(100000L)
            .build();

        System.out.println(user.getPassword());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        pointService.charging(email, chargeRequest);

        assertThat(user.getPoint()).isEqualTo(nowPoint + chargeRequest.getChargeAmount());
    }

    @DisplayName("포인트 출금 - 패스워드 불일치로 포인트 출금에 실패한다.")
    @Test
    public void pointWithdrawal_failure() {
        User user = createUser();
        String email = "test123@test.com";
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
            .withdrawalAmount(50000L)
            .password("test12345")
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(any(), any())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
            () -> pointService.withdrawal(email, withdrawalRequest));
    }

    @DisplayName("포인트 출금 - 포인트 출금에 성공한다.")
    @Test
    public void pointWithdrawal_success() {
        User user = createUser();
        Long nowPoint = user.getPoint();
        String email = "test123@test.com";

        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
            .withdrawalAmount(50000L)
            .password("test1234")
            .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(any(), any())).thenReturn(true);

        pointService.withdrawal(email, withdrawalRequest);

        assertThat(user.getPoint()).isEqualTo(nowPoint - withdrawalRequest.getWithdrawalAmount());
    }

    @DisplayName("구매자의 포인트 지출 기록이 생성된다.")
    @Test
    public void purchasePointPayment() {
        User buyer = createUser();
        Long price = 198000L;

        pointService.purchasePointPayment(buyer, price);

        verify(pointRepository, times(1)).save(any());
    }

    @DisplayName("구매자의 지출되었던 포인트 반환 기록이 생성된다.")
    @Test
    public void purchasePointReturn() {
        User buyer = createUser();
        Long price = 198000L;

        pointService.purchasePointPayment(buyer, price);

        verify(pointRepository, times(1)).save(any());
    }

    @DisplayName("판매자에게 포인트 지급 기록이 생성된다.")
    @Test
    public void salesPointReceive() {
        User seller = createUser();
        Long price = 198000L;

        pointService.purchasePointPayment(seller, price);

        verify(pointRepository, times(1)).save(any());
    }

    @DisplayName("포인트 차감 전체(구매/출금) 내역을 조회한다.")
    @Test
    public void getDeductionHistory() {
        User user = createUser();
        String email = "test123@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        List<PointHistoryDto> deductionHistory = pointService.getDeductionHistory(email);

        long count = createPointBreakDownMockData().stream()
            .filter(p -> p.getDivision().equals(PointDivision.WITHDRAW) || p.getDivision()
                .equals(PointDivision.PURCHASE_DEDUCTION))
            .count();

        assertThat(deductionHistory.size()).isEqualTo(count);
    }

    @DisplayName("포인트 증가 전체(판매/충전) 내역을 조회한다.")
    @Test
    public void getChargingHistory() {
        User user = createUser();
        String email = "test123@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        List<PointHistoryDto> chargingHistory = pointService.getChargingHistory(email);

        long count = createPointBreakDownMockData().stream()
            .filter(p -> p.getDivision().equals(PointDivision.CHARGE) || p.getDivision()
                .equals(PointDivision.SALES_REVENUE))
            .count();

        assertThat(chargingHistory.size()).isEqualTo(count);
    }


    @DisplayName("사용자의 포인트를 조회한다.")
    @Test
    public void getPoints() {
        User user = createUser();
        String email = "test123@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        Long userPoint = pointService.getUserPoint(email);

        assertThat(user.getPoint()).isEqualTo(userPoint);
    }
}