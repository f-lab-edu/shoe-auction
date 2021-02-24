package com.flab.shoeauction.controller.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressBookDto {

    private Long id;
    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    @Builder
    public AddressBookDto(Long id, String addressName, String roadNameAddress,
        String detailedAddress, String postalCode) {
        this.id = id;
        this.addressName = addressName;
        this.roadNameAddress = roadNameAddress;
        this.detailedAddress = detailedAddress;
        this.postalCode = postalCode;
    }
}
