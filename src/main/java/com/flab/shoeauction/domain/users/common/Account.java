package com.flab.shoeauction.domain.users.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 물건 판매 후 정산 받을 계좌 정보 은행명,예금주
 */

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    private String bankName;
    private String accountNumber;
    private String depositor;
}
