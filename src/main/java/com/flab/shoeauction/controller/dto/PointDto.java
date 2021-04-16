package com.flab.shoeauction.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointDto {
    private int point;

    @Builder
    public PointDto(int point) {
        this.point = point;
    }
}
