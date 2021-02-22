package com.flab.shoeauction.domain.AddressBook;

import com.flab.shoeauction.controller.dto.AddressBookDto;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도로명 주소,상세주소
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class Address {

    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    public void updateAddress(AddressBookDto requestDto) {
        this.addressName = requestDto.getAddressName();
        this.roadNameAddress = requestDto.getRoadNameAddress();
        this.detailedAddress = requestDto.getDetailedAddress();
        this.postalCode = requestDto.getPostalCode();
    }
}