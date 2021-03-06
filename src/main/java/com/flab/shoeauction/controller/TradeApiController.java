package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.MonthlyTradingVolumesResponse;
import com.flab.shoeauction.controller.dto.TradeDto.ReasonRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TrackingNumberRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeMonthSearchCondition;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.TradeDto.TradeSearchCondition;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.TradeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradeApiController {

    private final TradeService tradeService;

    @LoginCheck(authority = UserLevel.AUTH)
    @GetMapping("/{productId}")
    public TradeResource obtainResourceForBid(@CurrentUser String email,
        @PathVariable Long productId, double size) {
        TradeResource resourceForBid = tradeService.getResourceForBid(email, productId, size);
        return resourceForBid;
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/sell/bid")
    @ResponseStatus(HttpStatus.CREATED)
    public void salesBid(@CurrentUser String email, @RequestBody TradeDto.SaveRequest requestDto) {
        tradeService.createSalesBid(email, requestDto);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/buy/bid")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseBid(@CurrentUser String email,
        @RequestBody TradeDto.SaveRequest requestDto) {
        tradeService.createPurchaseBid(email, requestDto);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/buy")
    public void immediatePurchase(@CurrentUser String email,
        @RequestBody ImmediateTradeRequest requestDto) {
        tradeService.immediatePurchase(email, requestDto);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping("/sell")
    public void immediateSale(@CurrentUser String email,
        @RequestBody ImmediateTradeRequest requestDto) {
        tradeService.immediateSale(email, requestDto);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @DeleteMapping
    public void deleteTrade(@RequestBody ChangeRequest requestDto) {
        tradeService.deleteTrade(requestDto);
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PatchMapping("/{id}/receiving-tracking-number")
    public void updateReceivingTrackingNumber(@PathVariable Long id, @CurrentUser String email,
        @RequestBody TrackingNumberRequest requestDto) {
        tradeService.updateReceivingTrackingNumber(id, email, requestDto.getTrackingNumber());
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/warehousing")
    public void confirmWarehousing(@PathVariable Long id) {
        tradeService.confirmWarehousing(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/inspection-successful")
    public void inspectionSuccessful(@PathVariable Long id) {
        tradeService.inspectionSuccessful(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/inspection-failed")
    public void inspectionFailed(@PathVariable Long id, @RequestBody ReasonRequest requestDto) {
        tradeService.inspectionFailed(id, requestDto.getReason());
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/return-tracking-number")
    public void updateReturnTrackingNumber(@PathVariable Long id,
        @RequestBody TrackingNumberRequest requestDto) {
        tradeService.updateReturnTrackingNumber(id, requestDto.getTrackingNumber());
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("/{id}/forwarding-tracking-number")
    public void updateForwardingTrackingNumber(@PathVariable Long id,
        @RequestBody TrackingNumberRequest requestDto) {
        tradeService.updateForwardingTrackingNumber(id, requestDto.getTrackingNumber());
    }

    @LoginCheck(authority = UserLevel.AUTH)
    @PatchMapping("/{id}/purchase-confirmation")
    public void confirmPurchase(@PathVariable Long id, @CurrentUser String email) {
        tradeService.confirmPurchase(id, email);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping
    public Page<TradeInfoResponse> getTradeInfos(TradeSearchCondition tradeSearchCondition,
        Pageable pageable) {
        return tradeService.getTradeInfos(tradeSearchCondition, pageable);
    }

    @LoginCheck(authority =UserLevel.ADMIN)
    @GetMapping("/month-volumes")
    public List<MonthlyTradingVolumesResponse> getMonthVolumes(TradeMonthSearchCondition tradeMonthSearchCondition) {
        return tradeService.getMonthlyTradingVolumes(tradeMonthSearchCondition);
    }
}