package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.controller.dto.TradeDto.TradeBidResponse;
import com.flab.shoeauction.controller.dto.TradeDto.TradeCompleteInfo;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.common.Currency;
import com.flab.shoeauction.domain.product.common.OrderStandard;
import com.flab.shoeauction.domain.product.common.SizeClassification;
import com.flab.shoeauction.domain.product.common.SizeUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class ProductDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    @AllArgsConstructor
    public static class SaveRequest {

        @NotBlank(message = "제품 한글명을 입력해주세요.")
        private String nameKor;

        @NotBlank(message = "제품 영문명을 입력해주세요.")
        private String nameEng;

        @NotBlank(message = "모델 넘버를 입력해주세요.")
        private String modelNumber;

        @NotBlank(message = "색상을 입력해주세요.")
        private String color;

        @NotNull(message = "출시일을 입력해주세요.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate releaseDate;

        @Positive(message = "올바른 출시가를 입력해주세요.")
        @NotNull(message = "출시가를 입력해주세요.")
        private int releasePrice;

        @NotNull(message = "출시가 통화를 선택해주세요.")
        private Currency currency;

        @NotNull(message = "사이즈 분류를 선택해주세요.")
        private SizeClassification sizeClassification;

        @NotNull(message = "사이즈의 단위를 선택해주세요.")
        private SizeUnit sizeUnit;

        @Positive(message = "올바른 최소 사이즈를 입력해주세요.")
        @NotNull(message = "최소 사이즈를 입력해주세요.")
        private double minSize;

        @Positive(message = "올바른 최대 사이즈를 입력해주세요.")
        @NotNull(message = "최대 사이즈를 입력해주세요.")
        private double maxSize;

        @Positive(message = "올바른 사이즈 간격을 입력해주세요.")
        @NotNull(message = "사이즈 간격을 입력해주세요.")
        private double sizeGap;

        @NotNull(message = "브랜드를 선택해주세요.")
        private BrandInfo brand;

        private String originImagePath;

        private String thumbnailImagePath;

        private String resizedImagePath;

        public Product toEntity() {
            return Product.builder()
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
                .brand(this.brand.toEntity())
                .originImagePath(this.originImagePath)
                .thumbnailImagePath(this.thumbnailImagePath)
                .resizedImagePath(this.resizedImagePath)
                .build();
        }

        public void setImagePath(String originImagePath, String thumbnailImagePath,
            String resizedImagePath) {
            this.originImagePath = originImagePath;
            this.thumbnailImagePath = thumbnailImagePath;
            this.resizedImagePath = resizedImagePath;
        }

        public void deleteImagePath() {
            setImagePath(null, null, null);
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ProductInfoResponse {

        private Long id;
        private String nameKor;
        private String nameEng;
        private String modelNumber;
        private String color;
        private LocalDate releaseDate;
        private int releasePrice;
        private Currency currency;
        private SizeClassification sizeClassification;
        private SizeUnit sizeUnit;
        private double minSize;
        private double maxSize;
        private double sizeGap;
        private String resizedImagePath;
        private BrandInfo brand;
        private List<TradeBidResponse> saleBids = new ArrayList<>();
        private List<TradeBidResponse> purchaseBids = new ArrayList<>();
        private List<TradeCompleteInfo> tradeCompleteInfos = new ArrayList<>();

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class ProductInfoByTrade {

        private Long id;
        private String nameKor;
        private String nameEng;
        private String modelNumber;
        private String color;
        private BrandInfo brand;
        private TradeBidResponse immediatePurchasePrice;
        private TradeBidResponse immediateSalePrice;
    }

    @Getter
    @NoArgsConstructor
    public static class IdRequest {

        private Long id;

        @Builder
        public IdRequest(Long id) {
            this.id = id;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WishItemResponse {

        private Long id;
        private Long productId;
        private String nameKor;
        private String nameEng;
        private Brand brand;

        @Builder
        public WishItemResponse(Long id, Long productId, String nameKor, String nameEng,
            Brand brand) {
            this.id = id;
            this.productId = productId;
            this.nameKor = nameKor;
            this.nameEng = nameEng;
            this.brand = brand;
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThumbnailResponse {

        private Long id;
        private String productThumbnailImagePath;
        private String brandThumbnailImagePath;
        private String nameKor;
        private String nameEng;
        private Long lowestPrice;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCondition {

        private String keyword;
        private Long brandId;
        private OrderStandard orderStandard;
    }
}