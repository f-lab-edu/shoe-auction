package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.UserDto.TradeUserInfo;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.user.User;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TradeDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeBidResponse {

        private Long tradeId;
        private Long productId;
        private double productSize;
        private Long price;

        @Builder
        public TradeBidResponse(Long tradeId, Long productId, double productSize, Long price) {
            this.tradeId = tradeId;
            this.productId = productId;
            this.productSize = productSize;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChangeRequest {

        private Long tradeId;
        private Long price;

        @Builder
        public ChangeRequest(Long tradeId, Long price) {
            this.tradeId = tradeId;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
                .status(TradeStatus.PRE_CONCLUSION)
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
                .status(TradeStatus.PRE_CONCLUSION)
                .product(product)
                .publisher(user)
                .buyer(user)
                .shippingAddress(address)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TrackingNumberRequest {

        private String trackingNumber;

        @Builder
        public TrackingNumberRequest(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ReasonRequest {

        private String reason;

        @Builder
        public ReasonRequest(String reason) {
            this.reason = reason;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeSearchCondition {

        private Long tradeId;
        private String sellersEmail;
        private String buyersEmail;

        @Builder
        public TradeSearchCondition(Long tradeId, String sellersEmail, String buyersEmail) {
            this.tradeId = tradeId;
            this.sellersEmail = sellersEmail;
            this.buyersEmail = buyersEmail;
        }

        public boolean isSearchBySeller() {
            return (this.getBuyersEmail() == null && this.getTradeId() == null
                && this.getSellersEmail() != null);
        }

        public boolean isSearchByBuyer() {
            return (this.getSellersEmail() == null && this.getTradeId() == null
                && this.getBuyersEmail() != null);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeInfoResponse {

        private Long id;
        private TradeStatus status;

        @Builder
        public TradeInfoResponse(Long id, TradeStatus status) {
            this.id = id;
            this.status = status;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeCompleteInfo {

        private double productSize;
        private Long price;
        private LocalDateTime completeTime;

        @Builder
        public TradeCompleteInfo(double productSize, Long price, LocalDateTime completeTime) {
            this.productSize = productSize;
            this.price = price;
            this.completeTime = completeTime;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeMonthSearchCondition {
        private String year;
    }

    @Getter
    @NoArgsConstructor
    public static class MonthlyTradingVolumesResponse {
        private String date;
        private Long count;
    }
}

