package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.controller.dto.BrandDto.BrandInfo;
import com.flab.shoeauction.domain.brand.Brand;
import com.flab.shoeauction.domain.product.Currency;
import com.flab.shoeauction.domain.product.Product;
import com.flab.shoeauction.domain.product.SizeClassification;
import com.flab.shoeauction.domain.product.SizeUnit;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class ProductDto {

    @Getter
    @NoArgsConstructor
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
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfoResponse {

        private Long id;
        private String nameKor;
        private String nameEng;
        private String modelNumber;
        private String color;
        //@JsonDeserialize(using = LocalDateDeserializer.class)
        //@JsonSerialize(using = LocalDateSerializer.class)
        private LocalDate releaseDate;
        private int releasePrice;
        private Currency currency;
        private SizeClassification sizeClassification;
        private SizeUnit sizeUnit;
        private double minSize;
        private double maxSize;
        private double sizeGap;
        private BrandInfo brand;
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
    @NoArgsConstructor
    public static class WishItemResponse {

        private Long id;
        private Long productId;
        private String nameKor;
        private String nameEng;
        private Brand brand;

        @Builder
        public WishItemResponse(Long id, Long productId, String nameKor, String nameEng, Brand brand) {
            this.id = id;
            this.productId = productId;
            this.nameKor = nameKor;
            this.nameEng = nameEng;
            this.brand = brand;
        }
    }
}

}
