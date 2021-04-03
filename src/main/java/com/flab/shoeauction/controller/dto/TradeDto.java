package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TradeDto {

    @Getter
    @NoArgsConstructor
    public static class TradeResource {

        private TradeUserInfo tradeUserInfo;
        private ProductInfoByTrade ProductInfoByTrade;

        @Builder
        public TradeResource(
            TradeUserInfo tradeUserInfo, ProductInfoByTrade productInfoByTrade) {
            this.tradeUserInfo = tradeUserInfo;
            this.ProductInfoByTrade = productInfoByTrade;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeBidResponse {

        private Long id;
        private Long productId;
        private double productSize;
        private Long price;

        @Builder
        public TradeBidResponse(Long id, Long productId, double productSize, Long price) {
            this.id = id;
            this.productId = productId;
            this.productSize = productSize;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SaveRequest {

        private Long price;
        private double productSize;
        private Long productId; // DTO로 받도록 리팩토링
        private Long addressId;

        @Builder
        public SaveRequest(Long productId, double productSize, Long price,
            Long addressId) {
            this.productId = productId;
            this.productSize = productSize;
            this.price = price;
            this.addressId = addressId;
        }

        // 판매 입찰용
        public Trade toEntityBySeller(User user, Product product, Address address) {
            return Trade.builder()
                .price(this.price)
                .productSize(this.productSize)
                .status(TradeStatus.BID)
                .product(product)
                .publisher(user)
                .seller(user)
                .returnAddress(address)
                .build();
        }

        // 구매 입찰용
        public Trade toEntityByBuyer(User user, Product product, Address address) {
            return Trade.builder()
                .price(this.price)
                .productSize(this.productSize)
                .status(TradeStatus.BID)
                .product(product)
                .publisher(user)
                .buyer(user)
                .shippingAddress(address)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ImmediateTradeRequest {

        private Long tradeId;
        private Long addressId;
        private Long productId;

        @Builder
        public ImmediateTradeRequest(Long tradeId, Long addressId, Long productId) {
            this.tradeId = tradeId;
            this.addressId = addressId;
            this.productId = productId;
        }
    }

}