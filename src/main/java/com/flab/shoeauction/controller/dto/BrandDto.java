package com.flab.shoeauction.controller.dto;

import com.flab.shoeauction.domain.brand.Brand;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BrandDto {

    @Getter
    @NoArgsConstructor
    public static class SaveRequest {

        @NotBlank(message = "브랜드 한글명을 입력해주세요.")
        private String nameKor;
        @NotBlank(message = "브랜드 영문명을 입력해주세요.")
        private String nameEng;

        public Brand toEntity() {
            return Brand.builder()
                .nameKor(this.nameKor)
                .nameEng(this.nameEng)
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class BrandInfo {

        private Long id;
        private String nameKor;
        private String nameEng;

        public Brand toEntity() {
            return Brand.builder()
                .id(this.id)
                .nameKor(this.nameKor)
                .nameEng(this.nameEng)
                .build();
        }
    }
}
