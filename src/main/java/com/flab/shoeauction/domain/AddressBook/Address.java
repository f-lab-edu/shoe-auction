package com.flab.shoeauction.domain.AddressBook;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도로명 주소,상세주소
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;
}