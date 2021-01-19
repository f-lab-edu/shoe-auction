package com.flab.shoeauction.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * 물건 판매 후 정산 받을 계좌 정보 은행명,예금주
 */

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

  private String accountNumber;
  private String depositor;
}
