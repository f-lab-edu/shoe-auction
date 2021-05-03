package com.flab.shoeauction.domain.point;

/*
 * @ 포인트 구분
 * CHARGE: 충전(+)
 * WITHDRAW: 출금(-)
 * PURCHASE_DEDUCTION: 구매 대금(-)
 * RETURN: 구매 대금 반환(+)
 * SALES_REVENUE: 판매 대금(+)
 */

public enum PointDivision {
    CHARGE, WITHDRAW, PURCHASE_DEDUCTION, RETURN, SALES_REVENUE;
}
