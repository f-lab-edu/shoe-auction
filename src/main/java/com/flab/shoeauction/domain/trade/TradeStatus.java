package com.flab.shoeauction.domain.trade;

/**
 * BID : 구매 또는 판매를 등록하여 입찰 진행중인 상태
 * PROGRESS : 입찰 등록한 물품의 거래가 체결되어 거래가 진행중인 상태
 * END : 거래가 종료된 상태(또는 취소)
 */

public enum TradeStatus {
    BID, PROGRESS, END

}
