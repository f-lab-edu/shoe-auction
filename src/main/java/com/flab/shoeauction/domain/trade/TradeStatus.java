package com.flab.shoeauction.domain.trade;

/**
 *     PRE_CONCLUSION: 거래 체결 대기 (거래 체결과 동시에 결제까지 완료)
 *     PRE_SELLER_SHIPMENT: 판매자 발송 대기
 *     PRE_WAREHOUSING: 입고 대기(판매자 -> 회사)
 *     PRE_INSPECTION: 검수 대기
 *     PRE_SHIPMENT: 구매자 발송 대기
 *     SHIPPING: 배송중(회사 -> 구매자)
 *     TRADE_COMPLETE: 거래 완료
 *     CANCEL: 취소(판매자가 기간 내 상품 미발송 및 검수 탈락)
 */

public enum TradeStatus {
    PRE_CONCLUSION,
    PRE_SELLER_SHIPMENT,
    PRE_WAREHOUSING,
    PRE_INSPECTION,
    PRE_SHIPMENT,
    SHIPPING,
    TRADE_COMPLETE,
    CANCEL
}