package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.common.annotation.LoginCheck;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.ChangeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.ImmediateTradeRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.controller.dto.UserDto.TradeInfoResponse;
import com.flab.shoeauction.controller.dto.UserDto.TradeSearchCondition;
import com.flab.shoeauction.domain.users.common.UserLevel;
import com.flab.shoeauction.service.TradeService;
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
@RequestMapping("trades")
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
    @PatchMapping("{id}/receiving-tracking-number")
    public void updateReceivingTrackingNumber(@PathVariable Long id, @CurrentUser String email,
        @RequestBody String trackingNumber) {
        tradeService.updateReceivingTrackingNumber(id, email, trackingNumber);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("{id}/warehousing")
    public void confirmWarehousing(@PathVariable Long id) {
        tradeService.confirmWarehousing(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("{id}/inspection-successful")
    public void inspectionSuccessful(@PathVariable Long id) {
        tradeService.inspectionSuccessful(id);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("{id}/inspection-failed")
    public void inspectionFailed(@PathVariable Long id, @RequestBody String reason) {
        tradeService.inspectionFailed(id, reason);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @PatchMapping("{id}/return-tracking-number")
    public void updateReturnTrackingNumber(@PathVariable Long id,
        @RequestBody String trackingNumber) {
        tradeService.updateReturnTrackingNumber(id, trackingNumber);
    }

    @LoginCheck(authority = UserLevel.ADMIN)
    @GetMapping
    public Page<TradeInfoResponse> getTradeInfos(TradeSearchCondition tradeSearchCondition, Pageable pageable) {
        return tradeService.getTradeInfos(tradeSearchCondition, pageable);
    }
}