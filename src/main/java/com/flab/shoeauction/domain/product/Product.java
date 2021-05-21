package com.flab.shoeauction.domain.product;

import static java.util.stream.Collectors.toList;

import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoByTrade;
import com.flab.shoeauction.controller.dto.ProductDto.ProductInfoResponse;
import com.flab.shoeauction.controller.dto.ProductDto.SaveRequest;
import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeCompleteInfo;
import com.flab.shoeauction.domain.BaseTimeEntity;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import com.flab.shoeauction.domain.trade.Trade;
import com.flab.shoeauction.domain.trade.TradeStatus;
import com.flab.shoeauction.domain.users.user.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedEntityGraph;
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
@NamedEntityGraph()
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "BRAND_ID")
    private Brand brand;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trade> trades = new ArrayList<>();

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
            .resizedImagePath(this.resizedImagePath)
            .saleBids(getSaleBids())
            .purchaseBids(getPurchaseBids())
            .tradeCompleteInfos(getTradeCompletes())
            .brand(brand.toBrandInfo())
            .build();
    }

    private List<TradeCompleteInfo> getTradeCompletes() {
        return trades.stream()
            .filter(t->t.getStatus().equals(TradeStatus.TRADE_COMPLETE))
            .map(Trade::toTradeCompleteInfo)
            .sorted(Comparator.comparing(TradeCompleteInfo::getCompleteTime).reversed())
            .collect(toList());
    }


    public ProductInfoByTrade toProductInfoByTrade(User currentUser, double size) {
        return ProductInfoByTrade.builder()
            .id(this.id)
            .nameKor(this.nameKor)
            .nameEng(this.nameEng)
            .modelNumber(this.modelNumber)
            .color(this.color)
            .brand(brand.toBrandInfo())
            .immediatePurchasePrice(getLowestPrice(currentUser, size))
            .immediateSalePrice(getHighestPrice(currentUser, size))
            .build();
    }

    private TradeBidResponse getLowestPrice(User currentUser, double size) {
        return trades.stream()
            .filter(lowestPriceFilter(currentUser, size))
            .sorted(Comparator.comparing(Trade::getPrice))
            .map(Trade::toTradeBidResponse)
            .findFirst()
            .orElse(null);
    }

    private Predicate<Trade> lowestPriceFilter(User currentUser, double size) {
        return v -> v.getStatus() == TradeStatus.PRE_CONCLUSION && v.getBuyer() == null
            && v.getProductSize() == size && v.getPublisherId() != currentUser.getId();
    }

    private TradeBidResponse getHighestPrice(User currentUser, double size) {
        return trades.stream()
            .filter(highestPriceFilter(currentUser, size))
            .sorted(Comparator.comparing(Trade::getPrice).reversed())
            .map(Trade::toTradeBidResponse)
            .findFirst()
            .orElse(null);
    }

    private Predicate<Trade> highestPriceFilter(User currentUser, double size) {
        return v -> v.getStatus() == TradeStatus.PRE_CONCLUSION && v.getSeller() == null
            && v.getProductSize() == size && v.getPublisherId() != currentUser.getId();
    }


    private List<TradeBidResponse> getSaleBids() {
        return getTrades().stream()
            .filter(v -> v.getStatus() == TradeStatus.PRE_CONCLUSION && v.getBuyer() == null)
            .sorted(Comparator.comparing(Trade::getPrice))
            .map(Trade::toTradeBidResponse)
            .collect(toList());
    }

    private List<TradeBidResponse> getPurchaseBids() {
        return getTrades().stream()
            .filter(v -> v.getStatus() == TradeStatus.PRE_CONCLUSION && v.getSeller() == null)
            .sorted(Comparator.comparing(Trade::getPrice).reversed())
            .map(Trade::toTradeBidResponse)
            .collect(toList());
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