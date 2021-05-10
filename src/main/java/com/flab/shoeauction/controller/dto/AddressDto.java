package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.addressBook.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AddressDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveRequest {

        private Long id;
        private String addressName;
        private String roadNameAddress;
        private String detailedAddress;
        private String postalCode;

        @Builder
        public SaveRequest(Long id, String addressName, String roadNameAddress,
            String detailedAddress, String postalCode) {
            this.id = id;
            this.addressName = addressName;
            this.roadNameAddress = roadNameAddress;
            this.detailedAddress = detailedAddress;
            this.postalCode = postalCode;
        }


        public Address toEntity() {
            return Address.builder()
                .addressName(this.addressName)
                .detailedAddress(this.detailedAddress)
                .roadNameAddress(this.roadNameAddress)
                .postalCode(this.postalCode)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class IdRequest {
        private Long id;

        @Builder
        public IdRequest(Long id) {
            this.id = id;
        }
    }
}