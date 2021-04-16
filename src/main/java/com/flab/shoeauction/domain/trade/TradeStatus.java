package com.flab.shoeauction.domain.trade;

/**
 *     PRE_CONCLUSION: 거래 체결 대기 (거래 체결과 동시에 결제까지 완료)
 *     PRE_SELLER_SHIPMENT: 판매자 발송 대기
 *     PRE_WAREHOUSING: 입고 대기(판매자 -> 회사)
 *     PRE_INSPECTION: 검수 대기
 *     PRE_SHIPMENT: 구매자 발송 대기
 *     SHIPPING: 배송중(회사 -> 구매자)
 *     SHIPMENT_COMPLETE: 배송 완료
 *     TRADE_COMPLAETE: 거래 완료
 */

public enum TradeStatus {
    PRE_CONCLUSION,
    PRE_SELLER_SHIPMENT,
    PRE_WAREHOUSING,
    PRE_INSPECTION,
    PRE_SHIPMENT,
    SHIPPING,
    SHIPMENT_COMPLETE,
    TRADE_COMPLAETE
}