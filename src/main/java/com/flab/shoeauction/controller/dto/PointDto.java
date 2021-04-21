package com.flab.shoeauction.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointDto {

    private Long chargeAmount;

    @Builder
    public PointDto(Long chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
}