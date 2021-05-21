package com.flab.shoeauction.domain.trade;

import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeCompleteInfo;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.addressBook.Address;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.users.user.User;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLISHER_ID")
    private User publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private Long price;

    private double productSize;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETURN_ID")
    private Address returnAddress;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_ID")
    private Address shippingAddress;

    private String receivingTrackingNumber;

    private String forwardingTrackingNumber;

    private String returnTrackingNumber;

    private String cancelReason;

    @Builder
    public Trade(Long id, User publisher, User seller, User buyer,
        Product product, TradeStatus status, Long price, double productSize,
        Address returnAddress, Address shippingAddress) {
        this.id = id;
        this.publisher = publisher;
        this.seller = seller;
        this.buyer = buyer;
        this.product = product;
        this.status = status;
        this.price = price;
        this.productSize = productSize;
        this.returnAddress = returnAddress;
        this.shippingAddress = shippingAddress;
    }

    public TradeBidResponse toTradeBidResponse() {
        return TradeBidResponse.builder()
            .tradeId(this.id)
            .price(this.price)
            .productId(product.getId())
            .productSize(this.productSize)
            .build();
    }

    public TradeCompleteInfo toTradeCompleteInfo() {
        return TradeCompleteInfo.builder()
            .productSize(this.productSize)
            .price(this.price)
            .completeTime(this.getModifiedDate())
            .build();
    }

    public void makeImmediatePurchase(User buyer, Address shippingAddress) {
        this.shippingAddress = shippingAddress;
        this.buyer = buyer;
        this.status = TradeStatus.PRE_SELLER_SHIPMENT;
    }

    public void makeImmediateSale(User seller, Address returnAddress) {
        this.returnAddress = returnAddress;
        this.seller = seller;
        this.status = TradeStatus.PRE_SELLER_SHIPMENT;
    }

    public Long getPublisherId() {
        return publisher.getId();
    }

    public void updatePrice(Long price) {
        this.price = price;
    }

    public void updateReceivingTrackingNumber(String trackingNumber) {
        this.receivingTrackingNumber = trackingNumber;
    }

    public void updateReturnTrackingNumber(String trackingNumber) {
        this.returnTrackingNumber = trackingNumber;
    }

    public void updateCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public void updateStatus(TradeStatus status) {
        this.status = status;
    }

    public boolean isSellersEmail(String email) {
        return seller.isCurrentEmail(email);
    }

    public boolean isBuyersEmail(String email) {
        return buyer.isCurrentEmail(email);
    }

    public void recoverBuyerPoints(Long price) {
        buyer.chargingPoint(price);
    }

    public boolean isPurchaseBid() {
        return buyer != null;
    }

    public void cancelBecauseOfInspection(String reason) {
        this.cancelReason = reason;
        this.status = TradeStatus.CANCEL;
        buyer.chargingPoint(price);
    }

    public void updateStatusShipping(String trackingNumber) {
        this.forwardingTrackingNumber = trackingNumber;
        this.status = TradeStatus.SHIPPING;
    }

    public void endTrade() {
        this.status = TradeStatus.TRADE_COMPLETE;
        seller.chargingPoint(this.price);
    }
}