package com.flab.shoeauction.domain.users.common;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Point {
    private Integer totalPoint;
    private Integer bidAblePoint;

    public void charging(int point) {
        this.totalPoint += point;
        this.bidAblePoint += point;
    }
}