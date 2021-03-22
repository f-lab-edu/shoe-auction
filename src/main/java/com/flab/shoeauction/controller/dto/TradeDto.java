package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
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
    public static class SellResourceResponse {

        private TradeUserInfo tradeUserInfo;
        private ProductInfoResponse productInfo;
        // TODO :현재 사이즈 + 현재 프로덕트 중 최고가로 구매입찰 되어있는 프로덕트의 정보 (ID/PRICE)

        @Builder
        public SellResourceResponse(
            TradeUserInfo tradeUserInfo, ProductInfoResponse productInfo) {
            this.tradeUserInfo = tradeUserInfo;
            this.productInfo = productInfo;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SaveRequest {

        private TradeStatus status;
        private Long price;
        private Product product; // DTO로 받도록 리팩토링
        private Address address;

        @Builder
        public SaveRequest(Product product, TradeStatus status, Long price, Address address) {
            this.product = product;
            this.status = status;
            this.price = price;
            this.address = address;
        }

        // 판매 입찰용
        public Trade toEntityBySeller(User user) {
            return Trade.builder()
                .price(this.price)
                .status(TradeStatus.BID)
                .product(this.product)
                .publisher(user)
                .seller(user)
                .returnAddress(this.address)
                .build();
        }

        // 구매 입찰용
        public Trade toEntityByBuyer(User user) {
            return Trade.builder()
                .price(this.price)
                .status(TradeStatus.BID)
                .product(this.product)
                .publisher(user)
                .buyer(user)
                .shippingAddress(this.address)
                .build();
        }
    }
}