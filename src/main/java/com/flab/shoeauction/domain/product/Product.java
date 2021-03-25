package com.flab.shoeauction.domain.product;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfo;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 발매가의 통화 단위와 국가 별 사이즈 단위는 Enum 으로 관리한다. (추가와 삭제가 빈번하지 않기 때문)
 * 브랜드는 Enum 이 아닌 Table 로 분리했다. (추가의 간편함을 위하여)
 */

@Getter
@Builder
@AllArgsConstructor
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

    private String originImagePath;

    private String thumbnailImagePath;

    private String resizedImagePath;

    /*
     * 상품 조회 등의 Product를 사용 하는 비즈니스 로직은 대부분 Brand를 함께 사용한다.
     * 그렇기에 어차피 함께 사용할 Brand에 지연로딩을 설정하는 것은 조회 효율만 낮출 뿐이다.
     * 따라서 FetchType의 디폴트인 EAGER(즉시로딩)을 사용하여 조인쿼리로 조회하는 편이 좋다.
     */
    @ManyToOne
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<Trade> trades = new ArrayList<>();

    public ProductInfo toProductInfoResponse() {

        return ProductInfo.builder()
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
            .resizedImagePath(this.resizedImagePath)
            .saleBids(getSaleBids())
            .purchaseBids(getPurchaseBids())
            .brand(brand.toBrandInfo())
            .build();
    }


    public ProductInfoByTrade toProductInfoByTrade(double size) {
        return ProductInfoByTrade.builder()
            .id(this.id)
            .nameKor(this.nameKor)
            .nameEng(this.nameEng)
            .modelNumber(this.modelNumber)
            .color(this.color)
            .brand(brand.toBrandInfo())
            .immediatePurchasePrice(getLowestPrice(size)) //구매 BID 중 최고가격 (판매자 입장)
            .immediateSalePrice(getHighestPrice(size)) // 판매 BID중 최저 가격 (구매자 입장)
            .build();
    }

    private TradeBidResponse getLowestPrice(double size) {
        return trades.stream()
            .filter(v -> v.getStatus() == TradeStatus.BID && v.getBuyer() == null
                && v.getProductSize() == size)
            .sorted(Comparator.comparing(Trade::getPrice))
            .map(Trade::toTradeBidResponse)
            .findFirst()
            .orElse(null);
    }

    private TradeBidResponse getHighestPrice(double size) {
        return trades.stream()
            .filter(v -> v.getStatus() == TradeStatus.BID && v.getSeller() == null
                && v.getProductSize() == size)
            .sorted(Comparator.comparing(Trade::getPrice).reversed())
            .map(Trade::toTradeBidResponse)
            .findFirst()
            .orElse(null);
    }


    private List<TradeBidResponse> getSaleBids() {
        return getTrades().stream()
            .filter(v -> v.getStatus() == TradeStatus.BID && v.getBuyer() == null)
            .sorted(Comparator.comparing(Trade::getPrice))
            .map(Trade::toTradeBidResponse)
            .collect(Collectors.toList());
    }

    private List<TradeBidResponse> getPurchaseBids() {
        return getTrades().stream()
            .filter(v -> v.getStatus() == TradeStatus.BID && v.getSeller() == null)
            .sorted(Comparator.comparing(Trade::getPrice).reversed())
            .map(Trade::toTradeBidResponse)
            .collect(Collectors.toList());
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
        this.originImagePath = updatedProduct.getOriginImagePath();
        this.thumbnailImagePath = updatedProduct.getThumbnailImagePath();
        this.resizedImagePath = updatedProduct.getResizedImagePath();
    }


}