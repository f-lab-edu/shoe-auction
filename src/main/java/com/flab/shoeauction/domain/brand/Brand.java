package com.flab.shoeauction.domain.brand;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.BrandDto.SaveRequest;
import com.flab.shoeauction.domain.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Enum vs Table
 * Product 가 가지고 있는 브랜드를 정의할 때 Enum 대신 Table 로 분리하는 방법을 선택
 * Enum 을 사용하면 더 단순한 구조로 편하게 개발이 가능하지만
 * 브랜드 종류를 추가하려 할 때마다 코드를 수정해야 하기 때문에
 * 운영하는 입장에서는 매우 번거로울 수 있기 때문
 */

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String nameKor;

    @Column(unique = true)
    private String nameEng;

    private String originImagePath;

    private String thumbnailImagePath;

    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
            .id(this.id)
            .nameKor(this.nameKor)
            .nameEng(this.nameEng)
            .originImagePath(this.originImagePath)
            .thumbnailImagePath(this.thumbnailImagePath)
            .build();
    }

    public void update(SaveRequest updatedBrand) {
        this.nameKor = updatedBrand.getNameKor();
        this.nameEng = updatedBrand.getNameEng();
    }
}