package com.flab.shoeauction.controller;

import com.flab.shoeauction.common.annotation.CurrentUser;
import com.flab.shoeauction.controller.dto.TradeDto;
import com.flab.shoeauction.controller.dto.TradeDto.TradeResource;
import com.flab.shoeauction.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{productId}")
    public TradeResource getResourceOfProductSold(@CurrentUser String email,
        @PathVariable Long productId) {
        return tradeService.getSellResource(email, productId);
    }

    @PostMapping("/sell/bid")
    @ResponseStatus(HttpStatus.CREATED)
    public void salesBid(@CurrentUser String email, @RequestBody TradeDto.SaveRequest requestDto) {
        tradeService.createSalesBid(email, requestDto);
    }

    @PostMapping("/buy/bid")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseBid(@CurrentUser String email, @RequestBody TradeDto.SaveRequest requestDto) {
        tradeService.createPurchaseBid(email, requestDto);
    }
}