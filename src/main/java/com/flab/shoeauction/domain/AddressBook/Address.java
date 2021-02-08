package com.flab.shoeauction.domain.AddressBook;

import com.flab.shoeauction.controller.dto.UserDto.ChangeAddressRequest;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 도로명 주소,상세주소
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    public void updateAddress(ChangeAddressRequest requestDto) {
        this.addressName = requestDto.getAddressName();
        this.roadNameAddress = requestDto.getRoadNameAddress();
        this.detailedAddress = requestDto.getDetailedAddress();
        this.postalCode = requestDto.getPostalCode();
    }
}