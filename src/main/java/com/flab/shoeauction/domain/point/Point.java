package com.flab.shoeauction.domain.point;

import com.flab.shoeauction.controller.dto.PointDto.PointHistoryDto;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.users.user.User;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private PointDivision division;

    private Long amount;

    @Builder
    public Point(Long id, Long amount, User user,  PointDivision division) {
        this.id = id;
        this.amount = amount;
        this.user = user;
        this.division = division;
    }

    public PointHistoryDto toPointHistoryDto() {
        return PointHistoryDto.builder()
            .amount(this.amount)
            .division(this.division)
            .time(this.getCreatedDate())
            .build();
    }

}
