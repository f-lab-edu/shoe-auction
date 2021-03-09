package com.flab.shoeauction.domain.product;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.cart.Cart;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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


    /*
     * 상품 조회 등의 Product를 사용 하는 비즈니스 로직은 대부분 Brand를 함께 사용한다.
     * 그렇기에 어차피 함께 사용할 Brand에 지연로딩을 설정하는 것은 조회 효율만 낮출 뿐이다.
     * 따라서 FetchType의 디폴트인 EAGER(즉시로딩)을 사용하여 조인쿼리로 조회하는 편이 좋다.
     */
    @ManyToOne
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    /**
     * 일대다 단방향 매핑의 경우 엔티티가 관리하는 외래 키가 다른 테이블에 존재하고, 연관관계 관리를 위해 불필요한 update 쿼리문이 실행된다.
     * update 쿼리문이 실행된다고 해서 성능에 큰 영향이 있는것은 아니지만, 불필요한 쿼리가 실행됨에 따라 개발자에게 혼란을 줄 수 있기 때문에
     * 일대다 단방향의 경우 일대다 양방향 매핑을 사용하는 것이 좋다.
     * insertable과 updatable을 false로 설정하면 읽기 전용 필드로, 마치 양방향 매핑을 한 것과 같은 효과를 낼 수 있다.
     */
    @ManyToOne
    @JoinColumn(name = "CART_ID", insertable = false, updatable = false)
    private Cart cart;


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