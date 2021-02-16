package com.flab.shoeauction.domain.product;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.brand.Brand;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 발매가의 통화 단위와 국가 별 사이즈 단위는 Enum 으로 관리한다. (추가와 삭제가 빈번하지 않기 때문)
 * 브랜드는 Enum 이 아닌 Table 로 분리했다. (추가의 간편함을 위하여)
 */

@Getter
@NoArgsConstructor
@Entity

public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String nameKor;

    private String nameEng;

    @Column(unique = true)
    private String modelNumber;

    private String color;

    private LocalDate releaseDate;

    private int releasePrice;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private SizeClassification sizeClassification;

    @Enumerated(EnumType.STRING)
    private SizeUnit sizeUnit;

    private double minSize;

    private double maxSize;

    private double sizeGap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    @Builder
    public Product(String nameKor, String nameEng, String modelNumber, String color,
        LocalDate releaseDate, int releasePrice, Currency currency,
        SizeClassification sizeClassification, SizeUnit sizeUnit, double minSize, double maxSize,
        double sizeGap, Brand brand) {
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.modelNumber = modelNumber;
        this.color = color;
        this.releaseDate = releaseDate;
        this.releasePrice = releasePrice;
        this.currency = currency;
        this.sizeClassification = sizeClassification;
        this.sizeUnit = sizeUnit;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.sizeGap = sizeGap;
        this.brand = brand;
    }

    public ProductInfoResponse toProductInfoResponse() {
        return ProductInfoResponse.builder()
            .id(this.id)
            .nameKor(this.nameKor)
            .nameEng(this.nameEng)
            .modelNumber(this.modelNumber)
            .color(this.color)
            .releaseDate(this.releaseDate)
            .releasePrice(this.releasePrice)
            .currency(this.currency)
            .sizeClassification(this.sizeClassification)
            .sizeUnit(this.sizeUnit)
            .minSize(this.minSize)
            .maxSize(this.maxSize)
            .sizeGap(this.sizeGap)
            .brand(brand.toBrandInfo())
            .build();
    }

    public void update(SaveRequest updatedProduct) {
        this.nameKor = updatedProduct.getNameKor();
        this.nameEng = updatedProduct.getNameEng();
        this.modelNumber = updatedProduct.getModelNumber();
        this.color = updatedProduct.getColor();
        this.releaseDate = updatedProduct.getReleaseDate();
        this.releasePrice = updatedProduct.getReleasePrice();
        this.currency = updatedProduct.getCurrency();
        this.sizeClassification = updatedProduct.getSizeClassification();
        this.sizeUnit = updatedProduct.getSizeUnit();
        this.minSize = updatedProduct.getMinSize();
        this.maxSize = updatedProduct.getMaxSize();
        this.sizeGap = updatedProduct.getSizeGap();
        this.brand = updatedProduct.getBrand().toEntity();
    }
}