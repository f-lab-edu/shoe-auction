package com.flab.shoeauction.domain.addressBook;

import com.flab.shoeauction.controller.dto.AddressDto.SaveRequest;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue
    private Long id;
    private String addressName;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    public void updateAddress(SaveRequest requestDto) {
        this.addressName = requestDto.getAddressName();
        this.roadNameAddress = requestDto.getRoadNameAddress();
        this.detailedAddress = requestDto.getDetailedAddress();
        this.postalCode = requestDto.getPostalCode();
    }

    @Builder
    public Address(String addressName, String roadNameAddress, String detailedAddress,
        String postalCode) {
        this.addressName = addressName;
        this.roadNameAddress = roadNameAddress;
        this.detailedAddress = detailedAddress;
        this.postalCode = postalCode;
    }
}